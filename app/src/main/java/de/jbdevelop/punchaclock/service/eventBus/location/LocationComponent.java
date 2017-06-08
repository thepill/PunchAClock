package de.jbdevelop.punchaclock.service.eventBus.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import de.jbdevelop.punchaclock.db.WifiRepository;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.WifiHelper;
import de.jbdevelop.punchaclock.model.LocationDebugEntry;
import de.jbdevelop.punchaclock.model.Wifi;
import de.jbdevelop.punchaclock.service.eventBus.motion.MovementDetectedEvent;
import de.jbdevelop.punchaclock.service.eventBus.wifi.WifiConnectedEvent;

/**
 * Created by Jan on 30.03.2016.
 */
public class LocationComponent implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public final String LOGCAT_TAG = "PAC-" +  LocationComponent.class.getSimpleName();

    private static final int NUMBER_OF_REQUESTS = 1;
    private static final long INTERVAL_DURATION = 30 * 1000;
    public static final int MINIMUM_ACCURACY = 75;
    private static final long EXPIRATION_OFFSET = NUMBER_OF_REQUESTS * INTERVAL_DURATION + 60 * 1000;

    private Context context;
    private GoogleApiClient googleApiClient;

    public LocationComponent(Context context) {
        EventBus.getDefault().register(this);
        this.context = context;
        initGoogleAPIClient();
    }

    @Subscribe
    public void onRequestLocation(RequestLocationEvent event) {
        PACLog.i(LOGCAT_TAG, "Received RequestLocation event");
        startLocationUpdates();
    }

    @Subscribe
    public void onWifiConnected(WifiConnectedEvent event) {
        boolean wifiUnknown = !WifiHelper.isKnownWifi(event.getWifiInfo());
        if(wifiUnknown) {
            startLocationUpdates();
        }
    }

    private void initGoogleAPIClient(){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Subscribe
    public void onMovementDetected(MovementDetectedEvent event) {
        PACLog.i(LOGCAT_TAG, "Received MovementDetected event");
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if(!shouldRequestLocationFix()) {
            return;
        }

        PACLog.i(LOGCAT_TAG, "Requesting location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
    }

    private boolean shouldRequestLocationFix() {
        String logInfo = "Not requesting location updates because ";
        if(!locationPermissionsGranted()) {
            Log.i(LOGCAT_TAG, logInfo + " location permissions not granted");
            return false;
        }
        if(!googleApiClient.isConnected()) {
            Log.i(LOGCAT_TAG, logInfo + " google api client not connected");
            return false;
        }
        if(WifiHelper.currentlyConnectedToKnownWifi(context)) {
            Log.i(LOGCAT_TAG, logInfo + " connected to known wifi");
            return false;
        }

        return true;
    }

    private boolean locationPermissionsGranted() {
        return !(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(INTERVAL_DURATION)
                .setFastestInterval(INTERVAL_DURATION)
                .setNumUpdates(NUMBER_OF_REQUESTS)
                .setExpirationTime(SystemClock.elapsedRealtime() + EXPIRATION_OFFSET);
        return request;
    }

    @Override
    public void onConnected(Bundle bundle) {
        PACLog.i(LOGCAT_TAG, "Connected to google api client.");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        PACLog.e(LOGCAT_TAG, "Failed to connect to Google API Client, Error Code: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        PACLog.i(LOGCAT_TAG, "Received location fix: " + location);

        new LocationDebugEntry(location).save();

        if(location.getAccuracy() <= MINIMUM_ACCURACY) {
            postLocationFixEvent(location);
        }
        else {
            PACLog.i(LOGCAT_TAG, "Bad accuracy: " + location.getAccuracy());
        }
    }

    private void postLocationFixEvent(Location location) {
        EventBus.getDefault().post(new LocationFixEvent(location));
    }

}
