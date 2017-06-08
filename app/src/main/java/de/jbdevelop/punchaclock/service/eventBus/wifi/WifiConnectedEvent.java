package de.jbdevelop.punchaclock.service.eventBus.wifi;

import android.net.wifi.WifiInfo;

/**
 * Created by Jan on 30.03.2016.
 */
public class WifiConnectedEvent {
    private final WifiInfo wifiInfo;

    public WifiConnectedEvent(WifiInfo wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    public WifiInfo getWifiInfo() {
        return wifiInfo;
    }
}
