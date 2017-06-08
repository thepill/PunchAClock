package de.jbdevelop.punchaclock.ui.debug.map;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Date;
import java.util.Map;

/**
 * Created by jan on 04.11.15.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    Context context;
    Map<Marker, Location> markerLocationMap;
    Marker previousMarker;

    public InfoWindowAdapter(Context context, Map<Marker, Location> markerLocationMap) {
        this.context = context;
        this.markerLocationMap = markerLocationMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Location location = markerLocationMap.get(marker);

        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView accuracy = new TextView(context);
        accuracy.setText("Accuracy: " + location.getAccuracy() + " m");

        TextView time = new TextView(context);
        time.setText(new Date(location.getTime()).toString());

        TextView speed = new TextView(context);
        speed.setText("Speed: " + location.getSpeed() + " m/s");


        // TextView deltaTime = new TextView(context);
        // double delta = (location.getTime() - locations.get(index - 1).getLocation().getTime())/1000;
        // deltaTime.setText("Delta to prev: " + delta +  " s");

        info.addView(time);
        info.addView(accuracy);
        info.addView(speed);
       // info.addView(deltaTime);

        return info;
    }
}
