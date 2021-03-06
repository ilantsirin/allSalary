package com.yarik.salaryshare.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.yarik.salaryshare.R;
import com.yarik.salaryshare.model.SalaryLocation;
import com.yarik.salaryshare.utils.LocationHelper;

import java.io.IOException;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final static String EXTRA_LOCATION = "LOCATION";

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location, menu);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_create:
                this.sendLocation();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String locationAddress = intent.getStringExtra(SearchManager.QUERY);

            try {
                LatLng latLng = LocationHelper.getInstance().getCoordinateFromAddress(this, locationAddress);
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } catch (IOException | IndexOutOfBoundsException e) {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLngBounds ISRAEL_BOUNDS = new LatLngBounds(new LatLng(31.0111963, 34.4454602), new LatLng(32.6318939, 35.0304821));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ISRAEL_BOUNDS.getCenter(), 2));
        googleMap.setMyLocationEnabled(true);

        this.googleMap = googleMap;
    }

    private void sendLocation() {
        if (this.googleMap != null) {
            LatLng latLng = this.googleMap.getCameraPosition().target;
            SalaryLocation salaryLocation;

            try {
                salaryLocation = new SalaryLocation(latLng, LocationHelper.getInstance().getAddress(this, latLng));

                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_LOCATION, salaryLocation);
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (IOException | IndexOutOfBoundsException e) {
                Toast.makeText(this, "Couldn't find location.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }
}