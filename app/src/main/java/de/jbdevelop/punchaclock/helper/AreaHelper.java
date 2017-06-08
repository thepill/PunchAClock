package de.jbdevelop.punchaclock.helper;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.ui.main.AreaListAdapter;

/**
 * Created by Jan on 12.04.2016.
 */
public class AreaHelper {
    public static final String AREA_ID_KEY = "areaId";


    public static Area getAreaFromLocation(Location location) {

        for (Area area : AreaRepository.getInstance().getAllEnabled()) {
            double distanceToCenter = Haversine.getHaversineInMeters(location, area);
            if(distanceToCenter < area.getRadius()) {
                return area;
            }
        }
        return null;
    }

    public static Area getAreaFromIntent(Intent intent) {
        long areaId = intent.getExtras().getLong(AREA_ID_KEY);
        Area area = null;
        try {
            area = AreaRepository.getInstance().get(areaId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return area;
    }

    public static double getDistanceToCircumferenceOfClosestAreaInMeters(Location location){
        double result = Double.MAX_VALUE;

        for(Area area : AreaRepository.getInstance().getAllEnabled()){
            double distance = Haversine.getHaversineInMeters(location,area) - area.getRadius();
            if(distance < result){
                result = distance;
            }
        }

        return result;
    }
}
