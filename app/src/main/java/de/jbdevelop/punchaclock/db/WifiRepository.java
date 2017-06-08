package de.jbdevelop.punchaclock.db;

import com.activeandroid.query.Select;

import java.util.List;

import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.Wifi;

/**
 * Created by Benjamin on 05.04.16.
 */
public class WifiRepository implements IWifiRepository {
    private static WifiRepository instance;

    private WifiRepository() {}

    public static synchronized WifiRepository getInstance(){
        if(instance == null){
            instance = new WifiRepository();
        }
        return instance;
    }

    @Override
    public void insert(Wifi wifi) throws IllegalArgumentException {
        if(wifi == null){
            throw new IllegalArgumentException("argument wifi is null");
        }
        wifi.save();
    }

    @Override
    public Wifi get(long id) throws NoSuchFieldException {
        Wifi wifi = new Select()
                .from(Wifi.class)
                .where("id=?", id)
                .executeSingle();

        if(wifi == null){
            throw new NoSuchFieldException("wifi with id " + id + " not found");
        }

        return wifi;
    }

    @Override
    public void update(Wifi wifi) throws IllegalArgumentException {
        if(wifi == null){
            throw new IllegalArgumentException("argument wifi is null");
        }

        wifi.save();
    }

    @Override
    public void delete(Wifi wifi) throws NoSuchFieldException {
        if(wifi == null){
            throw new IllegalArgumentException("argument wifi is null");
        }

        Wifi storedWifi = null;
        try {
            storedWifi = get(wifi.getId());
        } catch (NoSuchFieldException e) {
            throw e;
        }

        storedWifi.delete();
    }

    @Override
    public List<Wifi> getAll() {
        return new Select()
                .from(Wifi.class)
                .execute();
    }

    @Override
    public List<Wifi> getAllWifiForArea(Area area) throws IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        return new Select().from(Wifi.class)
                .where(DatabaseContract.WifiContract.COLUMN_NAME_FOREIGNKEY_AREA.concat("=?"), area.getId())
                .execute();
    }

    @Override
    public Wifi getWifiBySSID(String ssid) throws IllegalArgumentException {
        if(ssid == null || ssid.isEmpty()){
            throw new IllegalArgumentException("argument ssid is null or empty");
        }

        return new Select().from(Wifi.class)
                .where(DatabaseContract.WifiContract.COLUMN_NAME_SSID.concat("=?"), ssid)
                .executeSingle();
    }

    @Override
    public Wifi getWifiByMAC(String mac) throws IllegalArgumentException {
        if(mac == null || mac.isEmpty()){
            throw new IllegalArgumentException("argument mac is null or empty");
        }

        return new Select().from(Wifi.class)
                .where(DatabaseContract.WifiContract.COLUMN_NAME_MAC.concat("=?"), mac)
                .executeSingle();
    }
}
