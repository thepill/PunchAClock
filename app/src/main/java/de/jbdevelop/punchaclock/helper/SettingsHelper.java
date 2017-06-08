package de.jbdevelop.punchaclock.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.jbdevelop.punchaclock.ui.setting.SettingsActivity;

/**
 * Created by Benjamin on 26.04.16.
 */
public class SettingsHelper{
    public static boolean isCalendarSyncEnabled(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsActivity.KEY_CALENDAR_SYNC,false);
    }

    public static void enableCalendarSync(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(SettingsActivity.KEY_CALENDAR_SYNC, true).apply();
    }

    public static int getMinMotionSleepSchedulDistance(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(SettingsActivity.KEY_MOTION_SCHEDULE_DISTANCE_METER, 1000);
    }

    public static int getMinMotionInterval(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(SettingsActivity.KEY_MOTION_SCHEDULE_MIN_TIME, 60);
    }

    public static boolean isMonitoringWifiEnabled(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsActivity.KEY_WIFI_MONITORING, true);
    }

    public static boolean isDebugEnabled(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsActivity.KEY_DEBUG, false);
    }

    public static Boolean toggleDebug(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean debugEnabled = sharedPreferences.getBoolean(SettingsActivity.KEY_DEBUG, false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean result = true;

        if(debugEnabled){
            editor.putBoolean(SettingsActivity.KEY_DEBUG, false);
            result = false;
        }else{
            editor.putBoolean(SettingsActivity.KEY_DEBUG, true);
        }

        editor.commit();
        return result;
    }

    public static boolean isServiceEnabled(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsActivity.KEY_SERVICE, true);
    }
}
