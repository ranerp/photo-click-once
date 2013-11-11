package com.twtchnz.OneClickPhoto;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.util.List;

public class LocationTracker extends Activity implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener
         {

    private OneClickPhotoMain parentActivity;

    private LocationClient locationClient;
    private LocationRequest locationRequest;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Geocoder geocoder;

    private boolean updatesRequested = false;

    private String addressString;

    public LocationTracker(OneClickPhotoMain parentActivity, LocationRequest locationRequest,
                           SharedPreferences preferences, SharedPreferences.Editor editor, Geocoder geocoder) {

        this.parentActivity = parentActivity;
        this.locationRequest = locationRequest;
        this.preferences = preferences;
        this.editor = editor;
        this.geocoder = geocoder;
        this.addressString = "";

        this.locationClient = new LocationClient(parentActivity, this, this);

        updatesRequested = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        (new GetAddressTask(this)).execute(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(parentActivity, "Connected", Toast.LENGTH_SHORT).show();
        onLocationChanged(locationClient.getLastLocation());

        start();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(parentActivity, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(parentActivity, OneClickPhotoUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        locationClient.connect();
    }

    public void disconnect() {
        locationClient.disconnect();
    }


    public void pause() {
        editor.putBoolean(OneClickPhotoUtils.KEY_UPDATES_REQUESTED, updatesRequested);
        editor.commit();
    }

    public void resume() {
        if (preferences.contains(OneClickPhotoUtils.KEY_UPDATES_REQUESTED))
            updatesRequested = preferences.getBoolean(OneClickPhotoUtils.KEY_UPDATES_REQUESTED, false);
        else {
            editor.putBoolean(OneClickPhotoUtils.KEY_UPDATES_REQUESTED, false);
            editor.commit();
        }
    }

    public void start() {
        updatesRequested = true;
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    public void stop() {
        updatesRequested = false;
        if(locationClient.isConnected())
            locationClient.removeLocationUpdates(this);

        locationClient.disconnect();
    }

    public Location getCurrentLocation() {
        Location location = locationClient.getLastLocation();

        return location;
    }

    public String getAddress() {
        return this.addressString;
    }

    protected class GetAddressTask extends AsyncTask<Location, Void, String> {

        Context localContext;

        public GetAddressTask(Context context) {
            super();

            localContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {

            List <Address> addresses;
            Location location = params[0];

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch(IOException e1) {
                Log.e(OneClickPhotoUtils.APPTAG, "IOException in GetAddressTask");
                e1.printStackTrace();

                return "IOException in GetAddressTask";

            } catch(IllegalArgumentException e2) {
                Log.e(OneClickPhotoUtils.APPTAG, "IllegalArgumentException in GetAddressTask");
                e2.printStackTrace();

                return "IllegalArgumentException in GetAddressTask";

            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                String addressText = address.getLocality() + " " + address.getCountryName() + " " +
                                     address.getAddressLine(0) + " " + address.getPostalCode();

                return addressText;
            }

            return "No address found";
        }

        @Override
        protected void onPostExecute(String address) {
            addressString = address;
        }
    }

}
