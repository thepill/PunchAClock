package de.jbdevelop.punchaclock.service.eventBus.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.model.LocationDebugEntry;
import de.jbdevelop.punchaclock.service.eventBus.wifi.WifiConnectedEvent;

/**
 * Created by Benjamin on 07.05.16.
 */
public class PassiveLocationComponent implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public final String LOGCAT_TAG = "PAC-" +  PassiveLocationComponent.class.getSimpleName();

    private Context context;
    private GoogleApiClient googleApiClient;
    private static boolean isActive;

    public PassiveLocationComponent(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
        isActive = false;
    }

    @Subscribe
    public void onStartPassiveLocationEvent(StartPassiveLocationEvent event) {
        if(isActive){
            return;
        }

        PACLog.i(LOGCAT_TAG, "Received StartPassiveLocation event");
        initGoogleAPIClient();
    }

    @Subscribe
    public void onStopPassiveLocationEvent(StopPassiveLocationEvent event) {
        PACLog.i(LOGCAT_TAG, "Received StopPassiveLocation event");
        stopLocationUpdates();
    }

    @Subscribe
    public void onWifiConnectedEvent(WifiConnectedEvent wifiConnectedEvent){
        PACLog.i(LOGCAT_TAG, "Received WifiConnected event");
        stopLocationUpdates();
    }

    private void initGoogleAPIClient(){
        if(googleApiClient != null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        PACLog.i(LOGCAT_TAG, "Connection suspended");
    }

    protected void startLocationUpdates() {

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
            isActive = true;
            PACLog.i(LOGCAT_TAG, "Started listening for passive location fixes");

        }
        catch (IllegalStateException ex){
            PACLog.i(LOGCAT_TAG, "Exception thrown while starting passive location updates - returning");
        }
    }

    protected void stopLocationUpdates(){
        if(!isActive){
            return;
        }

        PACLog.i(LOGCAT_TAG, "Removed passive location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
        isActive = false;
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_NO_POWER);
        return request;
    }

    @Override
    public void onLocationChanged(Location location) {
        PACLog.i(LOGCAT_TAG, "Received passive location fix");

        new LocationDebugEntry(location).save();

        if(location.getAccuracy() < LocationComponent.MINIMUM_ACCURACY){
            EventBus.getDefault().post(new LocationFixEvent(location));
        }else {
            PACLog.i(LOGCAT_TAG, "Bad accuracy: " + location.getAccuracy());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
