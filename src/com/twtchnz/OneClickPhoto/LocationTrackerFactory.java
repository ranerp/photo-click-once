package com.twtchnz.OneClickPhoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import com.google.android.gms.location.LocationRequest;

import java.util.Locale;

public class LocationTrackerFactory {

    public static LocationTracker build(OneClickPhotoMain parentActivity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(OneClickPhotoUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(OneClickPhotoUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        SharedPreferences preferences = parentActivity.getSharedPreferences(OneClickPhotoUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Geocoder geocoder = new Geocoder(parentActivity, Locale.getDefault());

        LocationTracker locationTracker = new LocationTracker(parentActivity, locationRequest,preferences, editor, geocoder);

        return locationTracker;
    }
}
