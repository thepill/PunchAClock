package de.jbdevelop.punchaclock.ui.setting;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.service.PACService;
import de.jbdevelop.punchaclock.service.StopServiceEvent;
import de.jbdevelop.punchaclock.service.eventBus.location.RequestLocationEvent;

/**
 * Created by Benjamin on 19.04.16.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_MOTION_SCHEDULE_DISTANCE_METER = "setting-motion-minDistance";
    public static final String KEY_MOTION_SCHEDULE_MIN_TIME= "setting-motion-minTime";

    public static final String KEY_CALENDAR_SYNC = "setting-calendar-sync";
    public static final String KEY_WIFI_MONITORING = "setting-wifi-monitoring";
    public static final String KEY_DEBUG = "setting-debug";
    public static final String KEY_VERSION = "setting-version";
    public static final String KEY_SERVICE = "setting-service";

    SharedPreferences prefs;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        //Observe changes on settings not onCreate because displaying the settings will trigger a change too...
        observe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void observe() {
        if(listener != null){
            return;
        }
        listener = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        onChange(sharedPreferences, key);
                    }
                };
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    private void onChange(SharedPreferences sharedPreferences, String key) {
        EventBus.getDefault().post(new SettingChangedEvent(sharedPreferences, key));
    }

    @Subscribe
    public void onSettingChanged(SettingChangedEvent event){
        switch (event.getKey()){
            case KEY_CALENDAR_SYNC:
                boolean syncEnabled = (boolean) event.getValue();
                if(syncEnabled){
                    requestCalendarPermission();
                }
                break;
            case KEY_SERVICE:
                changeServiceState((boolean)event.getValue());
            default:
                break;
        }
    }

    private void requestCalendarPermission(){
        if(permissionAlreadyGranted(Manifest.permission.WRITE_CALENDAR)){
            return;
        }

        requestPermission(Manifest.permission.WRITE_CALENDAR, 0);
    }

    private boolean permissionAlreadyGranted(String permissionName) {
        return (ContextCompat.checkSelfPermission(this,
                permissionName) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(String permissionName, int requestCode){
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName, permissionName}, requestCode);
    }

    private void changeServiceState(boolean enabled){
        if(!enabled){
            EventBus.getDefault().post(new StopServiceEvent());
        }
        else {
            final Intent pacService = new Intent(this, PACService.class);

            new Thread("PACService Thread") {
                @Override
                public void run() {
                    PACLog.i("PAC-Service", "Starting PACService");
                    startService(pacService);
                }
            }.start();
        }
    }
}
