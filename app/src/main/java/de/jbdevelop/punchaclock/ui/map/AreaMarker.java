package de.jbdevelop.punchaclock.ui.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by jan on 02.11.15.
 */
public class AreaMarker {
    private Marker marker;
    private Circle circle;

    AreaMarker(Area area, GoogleMap map) {
        LatLng position = area.getCoordinates();
        marker = map.addMarker(new MarkerOptions().position(position).title(area.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        circle = map.addCircle(new CircleOptions().center(position).radius(area.getRadius()).fillColor(0x407AD8FF).strokeColor(0x80ffffff));
    }

    void setPosition(LatLng position) {
        marker.setPosition(position);
        circle.setCenter(position);
    }

    void setRadius(double radius) {
        circle.setRadius(radius);
    }

    double getRadius() {
       return circle.getRadius();
    }

    LatLng getPosition() {
        return marker.getPosition();
    }
}
