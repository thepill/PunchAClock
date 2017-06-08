package de.jbdevelop.punchaclock.model;

import android.location.Location;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.DatabaseContract;
import de.jbdevelop.punchaclock.db.IAreaRepository;
import de.jbdevelop.punchaclock.helper.Haversine;

@Table(name = DatabaseContract.AreaContract.TABLE_NAME)
public class Area extends Model {

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_DESCRIPTION)
    private String description;

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_CREATEDON)
    private Date createdAt = new Date();

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_ENABLED)
    private boolean enabled = true;

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_COORDINATES_LATITUDE)
    private double latitude;

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_COORDINATES_LONGITUDE)
    private double longitude;

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_RADIUS)
    private double radius;

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_CURRENTACTIVE)
    private boolean currentActive = false;

    @Column(name = DatabaseContract.AreaContract.COLUMN_NAME_MONITORWIFI)
    private boolean monitorWifi;


    public Area() {
        super();
    }

    public Area(String description, LatLng coordinates, double radius) {
        super();
        this.description = description;
        setCoordinates(coordinates);
        this.radius = radius;
        this.monitorWifi = true;
    }

    @Override
    public String toString() {
        return  description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return createdAt;
    }

    public void setCreationDate(Date creationDate) {
        this.createdAt = creationDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setCoordinates(LatLng coordinates) {
        if (coordinates != null) {
            latitude = coordinates.latitude;
            longitude = coordinates.longitude;
        }
    }

    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isMonitoringWifi() {
        return monitorWifi;
    }

    public void setMonitorWifi(boolean monitorWifi) {
        this.monitorWifi = monitorWifi;
    }

    public static Area getClosestAreaFromCurrentPosition(IAreaRepository repository, Location currentLocation) {
        List<Area> areaList = repository.getAllEnabled();
        LatLng currentLocationCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (areaList.size() > 0) {
            Area closestArea = areaList.get(0);

            for (int i = 1; i < areaList.size(); i++) {
                if (Haversine.getHaversineInMeters(areaList.get(i).getCoordinates(),
                        currentLocationCoordinates) <
                        Haversine.getHaversineInMeters(closestArea.getCoordinates(), currentLocationCoordinates)) {
                    closestArea = areaList.get(i);
                }
            }

            return closestArea;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Area) {
            if(this.getId() == ((Area) obj).getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentActive() {
        return currentActive;
    }

    public void setCurrentActive(boolean currentActive) {
        this.currentActive = currentActive;
    }

    public boolean isLocationInArea(Location location){
        LatLng currentCoordinates = new LatLng(location.getLatitude(), location.getLongitude());

        double distanceInMeters = Haversine.getHaversineInMeters(currentCoordinates, this.getCoordinates());

        return distanceInMeters < this.radius ? true : false;
    }
}
