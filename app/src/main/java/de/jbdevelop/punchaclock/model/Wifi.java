package de.jbdevelop.punchaclock.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import de.jbdevelop.punchaclock.db.DatabaseContract;

/**
 * Created by Benjamin on 05.04.16.
 */
@Table(name = DatabaseContract.WifiContract.TABLE_NAME)
public class Wifi extends Model{

    @Column(name = DatabaseContract.WifiContract.COLUMN_NAME_FOREIGNKEY_AREA, index = true)
    private Area area;

    @Column(name = DatabaseContract.WifiContract.COLUMN_NAME_SSID)
    private String SSID;

    @Column(name = DatabaseContract.WifiContract.COLUMN_NAME_MAC)
    private String MAC;

    public Wifi() {
        super();
    }

    public Wifi(Area area, String ssid, String mac) {
        super();
        this.area = area;
        this.SSID = ssid;
        this.MAC = mac;
    }

    public Area getArea() {
        return area;
    }
    public String getSSID() {
        return SSID;
    }

    public void setSSID(String ssid) {
        this.SSID = ssid;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String mac) {
        this.MAC = mac;
    }


}
