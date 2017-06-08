package de.jbdevelop.punchaclock.db;

import java.util.List;

import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.Wifi;

/**
 * Created by Benjamin on 05.04.16.
 */
public interface IWifiRepository {
    void insert(Wifi wifi) throws IllegalArgumentException;
    Wifi get(long id) throws NoSuchFieldException;
    void update(Wifi wifi) throws IllegalArgumentException;
    void delete(Wifi wifi) throws NoSuchFieldException;
    List<Wifi> getAll();
    List<Wifi> getAllWifiForArea(Area area) throws IllegalArgumentException;
    Wifi getWifiBySSID(String ssid) throws IllegalArgumentException;
    Wifi getWifiByMAC(String mac) throws IllegalArgumentException;
}
