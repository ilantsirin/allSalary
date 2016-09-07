package com.yarik.salaryshare.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yarik.salaryshare.R;
import com.yarik.salaryshare.activities.LocationActivity;
import com.yarik.salaryshare.activities.MainActivity;
import com.yarik.salaryshare.activities.NavigationDrawerActivity;
import com.yarik.salaryshare.activities.ViewSalary;
import com.yarik.salaryshare.adapters.CustomAdapter;
import com.yarik.salaryshare.model.CurrentUser;
import com.yarik.salaryshare.model.Salary;
import com.yarik.salaryshare.model.SalaryLocation;
import com.yarik.salaryshare.network.FavoriteSalary;
import com.yarik.salaryshare.network.UnfavoriteSalary;
import com.yarik.salaryshare.utils.FeedContextMenuManager;
import com.yarik.salaryshare.utils.LocationHelper;
import com.yarik.salaryshare.utils.UtilHelper;
import com.yarik.salaryshare.views.FeedContextMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SalaryListFragment extends Fragment implements
        FeedContextMenu.OnFeedContextMenuItemClickListener {
    // Declare Variables
    private Activity activity;
    private Context context;

    // private Set<String> feedFavorites;
    private RecyclerView recycleView;
    private CustomAdapter listAdapter;
    private List<Salary> salaryList;
    private RecyclerView.LayoutManager lm;

    private SwipeRefreshLayout refreshLayout;

    //Location
   // Location location = LocationHelper.getInstance().getLocation(context);
    private static final float MetersToKilometers = 0.001f;

    //Peek
    private ProgressDialog pDialogMap;
    private static final String TAG = SalaryListFragment.class.getSimpleName();
    public static final String SEARCH_QUERY_ARG = "SEARCH_QUERY_TAG";
    public static final String LOCATION_ARG = "LOCATION_ARG";
    public static final int SELECT_LOCATION_REQUEST = 1;
    public static final String ACTION_PEEK = "SELECT_LOCATION_ACTION";
    private String searchQuery;
    private Location location;

    public static SalaryListFragment newInstance() {
        Bundle args = new Bundle();
        return newInstance(args);
    }

    public static SalaryListFragment newInstance(Bundle args) {
        SalaryListFragment fragment = new SalaryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SalaryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);

        setupReferences();
        Bundle args = getArguments();
        searchQuery = args.getString(SEARCH_QUERY_ARG, "");
        final SalaryLocation peekLocation = (SalaryLocation) args.getSerializable(LOCATION_ARG);

        if (!searchQuery.isEmpty()) {
            activity.setTitle("\""+searchQuery+"\"");
        }
        setLocation(peekLocation);
        salaryList = new ArrayList<>();
        //feedFavorites = new HashSet<>();
        setupRecycleView();
        querySalariesFeed();

    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            startLoginActivity();
        }
    }
    private void startLoginActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupReferences() {
        recycleView = (RecyclerView) activity.findViewById(R.id.salaryListView);
        refreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.salary_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySalariesFeed();
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 1);
    }

    private void setupRecycleView() {
        lm = new LinearLayoutManager(getActivity());
        recycleView.setLayoutManager(lm);
        listAdapter = new CustomAdapter(activity.getApplicationContext(), salaryList);
        recycleView.setAdapter(listAdapter);
        listAdapter.SetOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Salary salary = (Salary) v.getTag();
                Intent i = new Intent(context, ViewSalary.class);
                i.putExtra(ViewSalary.FIELD_NAME, salary.getField());
                i.putExtra(ViewSalary.POS_NAME, salary.getPosition());
                i.putExtra(ViewSalary.SALARY_NUM, salary.getSalary()); // Pass salary num
                i.putExtra(ViewSalary.EXP_NUM, salary.getExperience());     // Pass exp num
                i.putExtra(ViewSalary.SALARY_ID, salary.getSalaryId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);


            }
        });
    }

    private void setLocation(final SalaryLocation peekLocation) {
        if (peekLocation != null) {
            // If no location is set and there is a peek location...
            location = new Location("");
            location.setLatitude(peekLocation.latitude);
            location.setLongitude(peekLocation.longitude);
            //TODO: change the title to the name of the city
            //getActionBar().setTitle(peekLocation.city);
        } else {
            // If no peek location was provided, get the current one
            location = LocationHelper.getInstance().getLocation(context);
        }
    }


    /**
     * This starts the network queries. Goes in the order:
     * querySalariesFeed -> favorites -> salaries
     * TODO: Launch the likes and favorites asynchornously and then launch salary query when done.
     */
    private void querySalariesFeed() {
        queryFavorites();
    }
    private void queryFavorites() {
      /*  if (!UtilHelper.isNetworkOnline(context)) {
            UtilHelper.throwToastError(context, "No network connection.");
            refreshLayout.setRefreshing(false);
            return;
        }
        //Log.d(TAG, "Querying favorites");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e != null) {
                    UtilHelper.throwToastError(context, e);
                    refreshLayout.setRefreshing(false);
                    return;
                }

                feedFavorites.clear();
                //Log.d(TAG, "Found " + parseObjects.size() + " favorites");
                for (ParseObject obj : parseObjects) {
                    feedFavorites.add(obj.getString("salaryId"));
                }


            }
        });
        */
        querySalaries();
    }

    private void querySalaries() {
        if (!UtilHelper.isNetworkOnline(context)) {
            UtilHelper.throwToastError(context, "No network connection.");
            refreshLayout.setRefreshing(false);
            return;
        }
        if (location == null) {
            refreshLayout.setRefreshing(false);
            Toast.makeText(context, "Couldn't find location.", Toast.LENGTH_SHORT).show();
            return;
        }

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        double earthRadius = 6371;  // earth radius in km
        double radius = 100; // km
        final double lonMin = lon - Math.toDegrees(radius / earthRadius / Math.cos(Math.toRadians(lat)));
        final double lonMax = lon + Math.toDegrees(radius / earthRadius / Math.cos(Math.toRadians(lat)));
        final double latMax = lat + Math.toDegrees(radius / earthRadius);
        final double latMin = lat - Math.toDegrees(radius / earthRadius);

        ParseQuery<ParseObject> query = new ParseQuery<>("Salary");
        /** future filter
         query.whereGreaterThan("locationLatitude", latMin);
         query.whereGreaterThan("locationLongitude", lonMin);
         query.whereLessThan("locationLatitude", latMax);
         query.whereLessThan("locationLongitude", lonMax);
         */
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e != null) {
                    UtilHelper.throwToastError(context, e);
                    refreshLayout.setRefreshing(false);
                    return;
                }

                salaryList.clear();

                Log.d(TAG, "Found " + parseObjects.size() + " salaries");

                for (ParseObject salaryObject : parseObjects) {
                    String salaryDescriptor = salaryObject.getString("fieldName");
                    salaryDescriptor += " " + salaryObject.getString("posName");
                    if(salaryDescriptor.contains(searchQuery.toLowerCase())){
                        Salary salary = Salary.ParseToSalary(salaryObject);
                        Location feedLocation = new Location("");
                        feedLocation.setLatitude(salaryObject.getDouble("locationLatitude"));
                        feedLocation.setLongitude(salaryObject.getDouble("locationLongitude"));
                        salary.setDistance(feedLocation.distanceTo(location) * MetersToKilometers);
                        salaryList.add(salary);
                    }

                }


                listAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_salary_list, container, false);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);


        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        searchView.setSearchableInfo(searchableInfo);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_peek:
                Intent intent = new Intent(context, LocationActivity.class);
                pDialogMap = ProgressDialog.show(getActivity(), "", "Opening map...", true);
                startActivityForResult(intent, SELECT_LOCATION_REQUEST);
                return true;
            case R.id.action_filter_distance:
                Collections.sort(salaryList, new Comparator<Salary>() {
                    @Override
                    public int compare(Salary lhs, Salary rhs) {
                        if (lhs.getNumericDistance() > rhs.getNumericDistance())
                            return 1;
                        else if (lhs.getNumericDistance() < rhs.getNumericDistance())
                            return -1;
                        else
                            return 0;

                    }
                });
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.action_filter_new: {
                Collections.sort(salaryList, new Comparator<Salary>() {
                    @Override
                    public int compare(Salary lhs, Salary rhs) {
                        if (lhs.getCreatedAt().before(rhs.getCreatedAt()))
                            return 1;
                        else if (lhs.getCreatedAt().after(rhs.getCreatedAt()))
                            return -1;
                        else
                            return 0;
                    }
                });
                listAdapter.notifyDataSetChanged();
                break;
            }
        }

            return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        context = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pDialogMap != null) {
            pDialogMap.dismiss();
        }

        if (requestCode == SELECT_LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            SalaryLocation location = (SalaryLocation) data.getSerializableExtra(LocationActivity.EXTRA_LOCATION);
            Intent intent = new Intent(context, NavigationDrawerActivity.class);
            intent.setAction(ACTION_PEEK);
            intent.putExtra(LocationActivity.EXTRA_LOCATION, location);
            startActivity(intent);
        }
    }


    @Override
    public void onReportClick(Salary salary) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onShareClick(Salary salary) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(Salary salary) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onFavoritesClick(Salary salary) {
        if (!CurrentUser.getInstance().isLoggedIn()) {
            Toast.makeText(context, "Please log in before.", Toast.LENGTH_SHORT).show();
            return;
        }

        FeedContextMenuManager.getInstance().hideContextMenu();
        if (salary.isFavorited()) {
            UnfavoriteSalary.unfavoriteSalary(salary);
        } else {
            FavoriteSalary.favoriteFeed(salary);
        }
    }

    public void onMoreClick(View v, Salary salary) {
        // Log.d(TAG, "More clicked");
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, salary, this);
    }

}