package de.jbdevelop.punchaclock.service.job;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.WifiRepository;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.WifiHelper;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.Wifi;

/**
 * Created by Benjamin on 15.04.16.
 */
public class WifiDetectionJob extends Job {
    public static final String TAG = "PAC-WifiDetectionJob";
    public static final String LOGCAT_TAG = "PAC-" + WifiDetectionJob.class.getSimpleName();


    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Long areaId = params.getExtras().getLong("area-id", -1);

        if(areaId == -1){
            return null;
        }

        Area area = null;
        try {
            area = AreaRepository.getInstance().get(areaId);
        } catch (NoSuchFieldException e) {
            PACLog.e(LOGCAT_TAG, e.getStackTrace().toString());
            return null;
        }

        checkKnownWififorArea(area);
        return Result.SUCCESS;
    }

    private void checkKnownWififorArea(Area area){
        WifiInfo currentWifiInfo = getWifiInfo();
        boolean notProperlyConnected = !WifiHelper.isProperlyConnected(currentWifiInfo);
        boolean isUnknownWifi = !WifiHelper.isKnownWifi(currentWifiInfo);

        if(notProperlyConnected){
            PACLog.i(LOGCAT_TAG,"Not properly connected to wifi");
            return;
        }
        if(isUnknownWifi){
            addNewWifiToDatabase(area, currentWifiInfo);
        }
    }

    private WifiInfo getWifiInfo(){
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }

    private void addNewWifiToDatabase(Area area, WifiInfo wifiInfo){
        Wifi newWifi = new Wifi(area, wifiInfo.getSSID(), wifiInfo.getBSSID());
        WifiRepository.getInstance().insert(newWifi);

        PACLog.i(LOGCAT_TAG, "New wifi for area " + area.getDescription() + " added (SSID: " + wifiInfo.getSSID() + ")");
    }
}
