package de.jbdevelop.punchaclock.service.eventBus.motion;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import de.jbdevelop.punchaclock.helper.AreaHelper;
import de.jbdevelop.punchaclock.helper.MotionHelper;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.service.eventBus.location.LocationFixEvent;
import de.jbdevelop.punchaclock.service.eventBus.location.PassiveLocationComponent;
import de.jbdevelop.punchaclock.service.eventBus.location.StartPassiveLocationEvent;
import de.jbdevelop.punchaclock.service.eventBus.location.StopPassiveLocationEvent;
import de.jbdevelop.punchaclock.service.eventBus.wifi.WifiConnectedEvent;
import de.jbdevelop.punchaclock.service.eventBus.wifi.WifiDisconnectedEvent;
import de.jbdevelop.punchaclock.service.job.MotionDetectionJob;
import de.jbdevelop.punchaclock.ui.setting.SettingsActivity;

/**
 * Created by Jan on 30.03.2016.
 */
public class MotionComponent extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final long POLL_INTERVAL = 45 * 1000;
    private final String INTENT_ACTION_MOTION= "de.jbdevelop.broadcast.motionEvent";
    public final String LOGCAT_TAG = "PAC-" + MotionComponent.class.getSimpleName();

    private Context context;
    private GoogleApiClient googleApiClient;
    private PendingIntent pendingIntent;

    public MotionComponent(Context context) {
        EventBus.getDefault().register(this);
        this.context = context;
        initGoogleAPIClient();
        context.registerReceiver(this, new IntentFilter(INTENT_ACTION_MOTION));
    }

    @Subscribe
    public void onWifiConnected(WifiConnectedEvent event) {
        PACLog.i(LOGCAT_TAG, "Received WifiConnected event");
        stopListening();
    }

    @Subscribe
    public void onWifiDisconnected(WifiDisconnectedEvent event) {
        PACLog.i(LOGCAT_TAG, "Received WifiDisconnected event");
        startListening();
    }

    private void startListening() {
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }

        googleApiClient.connect();
        //returns to onConnected callback if succeeded and starts requesting activity updates
    }

    private void stopListening() {
        JobManager.instance().cancelAllForTag(MotionDetectionJob.TAG);

        if(pendingIntent != null){
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient, pendingIntent);
            PACLog.i(LOGCAT_TAG, "Removed activity updates from Google API Client");
        }
        if(!googleApiClient.isConnected()){
            googleApiClient.disconnect();
            PACLog.i(LOGCAT_TAG, "Disconnected from Google API Client");
        }
    }

    public void postMovementDetectedEvent() {
        EventBus.getDefault().post(new MovementDetectedEvent());
    }

    private void initGoogleAPIClient(){
        googleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        PACLog.i(LOGCAT_TAG, "Connected to Google API Client");

        Intent intent = new Intent(INTENT_ACTION_MOTION);
        pendingIntent = PendingIntent.getBroadcast(this.context,0,intent,0);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, POLL_INTERVAL, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        PACLog.i(LOGCAT_TAG, "Connection to Google API Client suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        PACLog.e(LOGCAT_TAG, "Failed to connect to Google API Client, Error Code: " + connectionResult.getErrorCode());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        PACLog.d(LOGCAT_TAG, "Movement detected " + result.getMostProbableActivity().toString());

        int typeOfMovement = result.getMostProbableActivity().getType();

        if(isMovingActivity(typeOfMovement)){
            EventBus.getDefault().post(new StopPassiveLocationEvent());
            postMovementDetectedEvent();
        }
        else if(isStill(typeOfMovement)) {
            EventBus.getDefault().post(new StartPassiveLocationEvent());
        }
    }

    private boolean isMovingActivity(int typeOfMovement){
        return typeOfMovement != DetectedActivity.STILL && typeOfMovement != DetectedActivity.TILTING
                && typeOfMovement != DetectedActivity.UNKNOWN;
    }

    private boolean isStill(int typeOfMovement){
        return typeOfMovement == DetectedActivity.STILL;
    }

    @Subscribe
    public void onLocationFixEvent(LocationFixEvent locationFixEvent){
        PACLog.i(LOGCAT_TAG, "Received LocationFix event");
        double distanceOfNearestArea = AreaHelper.getDistanceToCircumferenceOfClosestAreaInMeters(locationFixEvent.getLocation());
        double lastVelocity = locationFixEvent.getLocation().getSpeed();
        if(distanceOfNearestArea == Double.MAX_VALUE){
            PACLog.i(LOGCAT_TAG, "Couldn't find closest area");
            return;
        }

        int minDistanceMeter = SettingsHelper.getMinMotionSleepSchedulDistance(this.context);
        if(distanceOfNearestArea > minDistanceMeter){
            PACLog.i(LOGCAT_TAG, "Distance to next area: " + distanceOfNearestArea + " m | velocity: " + lastVelocity + " | let motion sensor sleep");
            stopListening();
            scheduleMotionDetectionJob(distanceOfNearestArea, lastVelocity);
        }else {
            PACLog.i(LOGCAT_TAG, "Distance to next area is below " + minDistanceMeter + " m | motion sensor not sleeping");
            startListening();
        }

    }

    private void scheduleMotionDetectionJob(double distanceOfNearestArea, double velocity){
        long nextRun = MotionHelper.getNextRuntimeInSeconds(context, distanceOfNearestArea, velocity) * 1_000;

        new JobRequest.Builder(MotionDetectionJob.TAG)
                .setExecutionWindow(nextRun, nextRun + 10_000)
                .build()
                .schedule();

        PACLog.i(LOGCAT_TAG, "Sleeping till " + new Date(System.currentTimeMillis() + nextRun));
    }

    @Subscribe
    public void onStartMotionDetectionEvent(StartMotionDetectionEvent startMotionDetectionEvent){
        startListening();
    }

}
