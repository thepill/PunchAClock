package de.jbdevelop.punchaclock.helper;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.WifiRepository;
import de.jbdevelop.punchaclock.model.Wifi;

/**
 * Created by Jan on 15.04.2016.
 */
public class WifiHelper {
    public static final String UNKNOWN_MAC = "00:00:00:00:00:00";

    public static boolean isKnownWifi(WifiInfo wifiInfo) {
        if(!isProperlyConnected(wifiInfo)){
            return false;
        }

        return WifiRepository.getInstance().getWifiByMAC(wifiInfo.getBSSID()) != null;
    }

    public static boolean isProperlyConnected(WifiInfo wifiInfo){
        return wifiInfo != null && wifiInfo.getBSSID() != null && !wifiInfo.getBSSID().equals(UNKNOWN_MAC) ? true : false;
    }

    public static boolean currentlyConnectedToKnownWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        return isKnownWifi(wifiInfo);
    }
}
