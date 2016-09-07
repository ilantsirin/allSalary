package com.yarik.salaryshare.utils;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yarik.salaryshare.BuildConfig;

import java.io.IOException;
import java.util.List;

public class LocationHelper {
    static LocationHelper instance;

    public static LocationHelper getInstance() {
        if (instance == null) {
            instance = new LocationHelper();
        }
        return instance;
    }

    public Location getLocation(Context context) {
        //TODO: get a real location!!
        if (BuildConfig.DEBUG) {
            Location location = new Location("location");
            location.setLatitude(32.066158);
            location.setLongitude(34.444219);
            return location;
        }

        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        List<String> matchingProviders = locManager.getAllProviders();
        for (String provider : matchingProviders) {
            Location location = locManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();
                if (accuracy < bestAccuracy) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                } else if (bestAccuracy == Float.MAX_VALUE && time > bestTime) {
                    bestResult = location;
                    bestTime = time;
                }
            }
        }
        if (bestResult == null) {
            Log.d("Location", "Couldn't get current location");
        }
        return bestResult;
    }

    public Address getAddress(Context context, LatLng latLng)
            throws IOException, IndexOutOfBoundsException {
        Geocoder geocoder = new Geocoder(context);
        Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);

        return address;
    }

    public LatLng getCoordinateFromAddress(Context context, String locationAddress) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        Address address = geocoder.getFromLocationName(locationAddress, 1).get(0);

        return new LatLng(address.getLatitude(), address.getLongitude());
    }
}