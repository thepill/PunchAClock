package de.jbdevelop.punchaclock.model;

import android.location.Location;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "DebugLocation")
public class LocationDebugEntry extends Model {

    @Column(name = "CreatedOn")
    private Date createdOn = new Date();

    @Column(name = "Provider")
    private String provider;

    @Column(name = "Time")
    private long time;

    @Column(name = "ElapsedRealTimeNanos")
    private long ElapsedRealTimeNanos;


    @Column(name = "Altitude")
    private double altitude;

    @Column(name = "Longitude")
    private double longitude;

    @Column(name = "Latitude")
    private double latitude;

    @Column(name = "Speed")
    private float speed;

    @Column(name = "Accuracy")
    private float accuracy;

    @Column(name = "Bearing")
    private float bearing;


    public LocationDebugEntry() {
    }

    public LocationDebugEntry(Location location) {
        setValues(location);
    }

    private void setValues(Location location) {
        provider = location.getProvider();
        time = location.getTime();
        altitude = location.getAltitude();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();
        bearing = location.getBearing();
        //ElapsedRealTimeNanos = location.getElapsedRealtimeNanos(); //Min sdk = 17
    }

    public Location getLocation() {
        Location locationToReturn = new Location(provider);
        locationToReturn.setTime(time);
        locationToReturn.setAltitude(altitude);
        locationToReturn.setLongitude(longitude);
        locationToReturn.setLatitude(latitude);
        locationToReturn.setSpeed(speed);
        locationToReturn.setAccuracy(accuracy);

        return locationToReturn;
    }


    public static List<LocationDebugEntry> fetchAll() {
        return new Select().from(LocationDebugEntry.class).execute();
    }
}
