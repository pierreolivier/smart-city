package com.smartcity.engine.manager;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.smartcity.engine.manager.location.GetAddressTask;

/**
 * Created by po on 10/14/14.
 */
public class LocationManager implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 3 * 1000;

    private LocationClient mLocationClient;
    private final LocationRequest mLocationRequest;
    private Location mLastLocation;

    public LocationManager() {
        super();

        mLocationClient = new LocationClient(Manager.activity(), this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    public void connect() {
        mLocationClient.connect();
    }

    public void disconnect() {
        stopLocationUpdate();

        mLocationClient.disconnect();
    }

    public void startLocationUpdate() {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    public void stopLocationUpdate() {
        if (mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("location", "connected");
    }

    @Override
    public void onDisconnected() {
        Log.e("location", "disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(Manager.activity(), 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.v("location", "error: " + connectionResult.getErrorCode());
        }
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

        mLastLocation = location;

        new GetAddressTask().execute(mLastLocation);

        Log.v("location test", msg);
    }
}
