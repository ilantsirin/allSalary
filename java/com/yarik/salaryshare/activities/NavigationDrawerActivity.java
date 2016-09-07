package com.yarik.salaryshare.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;
import com.yarik.salaryshare.R;
import com.yarik.salaryshare.application.ParseApplication;
import com.yarik.salaryshare.fragments.AddSalaryFragment;
import com.yarik.salaryshare.fragments.SalaryListFragment;
import com.yarik.salaryshare.model.SalaryLocation;

import org.json.JSONException;
import org.json.JSONObject;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navLayout;
    private Fragment fragment1, fragment2, fragment3;

    private ProfilePictureView userProfilePicture;
    private TextView userName;

    //Peek
    private SalaryLocation peekLocation;
    private boolean active;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_navigation);
        setupReferences();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        try {
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setSubtitle(getString(R.string.subtitle));
            actionBar.setDisplayShowTitleEnabled(true);
        } catch (Exception ignored) {
        }

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navLayout= (NavigationView)findViewById(R.id.navLayout);
        navLayout.setNavigationItemSelectedListener(this);

        active = false;
        peekLocation = (SalaryLocation) getIntent().getSerializableExtra(SalaryListFragment.LOCATION_ARG);
        handleIntent(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            makeMeRequest();
        }

        fragment1 = new AddSalaryFragment();
       // fragment2 = new Fragment2();
       // fragment3 = new Fragment3();

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.frameLayout, SalaryListFragment.newInstance());
        tx.commit();

    }
    @Override
    public  boolean onNavigationItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.add_item:
                setFragment(fragment1);
                break;
            case R.id.profile:
                Intent intent = new Intent(this, UserDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.navigation_item_4:
                logout();
                break;

        }
        return false;
    }
    private void setupReferences(){
        userProfilePicture = (ProfilePictureView) findViewById(R.id.userProfilePicture);
        userName = (TextView) findViewById(R.id.userName);

    }
    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack("mainFragment")
                .commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }


    private void queryProfile(String userId) {
        /* TODO: change the profile picture to a Circle
                Picasso.with(getApplicationContext()).load(FacebookUser.fbIdtoPhotoUrl(currentUser.getString("fbId")))
                        .centerCrop().placeholder(R.drawable.ic_action_account_circle)
                        .resize(AVATAR_SIZE, AVATAR_SIZE).transform(new CircleTransformation()).
                        into(userProfilePicture);
            }
        });
        */
    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }
    private void startLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (active) {
            // Only handles the Intent again if the Activity was already active
            handleIntent(intent);
        }
    }
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SalaryListFragment.LOCATION_ARG, peekLocation);
        }

        active = true;
    }
    private void handleIntent(Intent intent) {
        Bundle args = new Bundle();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            args.putString(SalaryListFragment.SEARCH_QUERY_ARG, searchQuery);
            args.putSerializable(SalaryListFragment.LOCATION_ARG, peekLocation);
        } else if (SalaryListFragment.ACTION_PEEK.equals(intent.getAction())) {
            peekLocation = (SalaryLocation) intent.getSerializableExtra(LocationActivity.EXTRA_LOCATION);
            args.putSerializable(SalaryListFragment.LOCATION_ARG, peekLocation);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, SalaryListFragment.newInstance(args)).commit();
    }
    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));


                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {
                                Log.d(ParseApplication.TAG,
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(ParseApplication.TAG,
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d(ParseApplication.TAG,
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d(ParseApplication.TAG,
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });

        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {

                if (userProfile.has("facebookId")) {
                    userProfilePicture.setProfileId(userProfile.getString("facebookId"));
                } else {
                    // Show the default, blank user profile picture
                    userProfilePicture.setProfileId(null);
                }

                if (userProfile.has("name")) {
                    userName.setText(userProfile.getString("name"));
                } else {
                    userName.setText("");
                }

            } catch (JSONException e) {
                Log.d(ParseApplication.TAG, "Error parsing saved user data.");
            }
        }
    }

}