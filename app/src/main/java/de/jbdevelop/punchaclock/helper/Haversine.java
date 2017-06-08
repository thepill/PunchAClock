package de.jbdevelop.punchaclock.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import de.jbdevelop.punchaclock.model.Area;

public class Haversine {
    public static final double EARTHRADIUS_IN_KILOMETERS = 6372.8;

    public static double getHaversineInMeters(Area from, Area to) {
        return getHaversineInMeters(from.getCoordinates(), to.getCoordinates());
    }

    public static double getHaversineInMeters(Location from, Location to) {
        LatLng fromLatLng = new LatLng(from.getLatitude(), from.getLongitude());
        LatLng toLatLng = new LatLng(to.getLatitude(), to.getLongitude());
        return getHaversineInMeters(fromLatLng, toLatLng);
    }

    public static double getHaversineInMeters(Location location, Area area) {
        return getHaversineInMeters(area.getCoordinates(), new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public static double getHaversineInMeters(LatLng from, LatLng to) {
        double dLat = Math.toRadians(to.latitude - from.latitude);
        double dLon = Math.toRadians(to.longitude - from.longitude);
        double lat1 = Math.toRadians(from.latitude);
        double lat2 = Math.toRadians(to.latitude);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTHRADIUS_IN_KILOMETERS * c * 1000;
    }

}
