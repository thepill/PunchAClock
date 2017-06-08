package de.jbdevelop.punchaclock.helper;

import android.content.Context;

/**
 * Created by Benjamin on 19.04.16.
 */
public class MotionHelper {
    private static final double DEFAULT_VELOCITY_METER_PER_SECOND = 40;
    private static final double MIN_SPEED_METER_PER_SECOND = 3;

    public static long getNextRuntimeInSeconds(Context context, double distanceInMeters, double velocity){
        int minInterval = SettingsHelper.getMinMotionInterval(context);


        if(distanceInMeters < 0 || velocity < 0){
            throw new IllegalArgumentException("distanceInMeters or velocity is negativ!");
        }

        if(velocity < MIN_SPEED_METER_PER_SECOND){
            velocity = DEFAULT_VELOCITY_METER_PER_SECOND;
        }

        long result = (long) (distanceInMeters / velocity);
        if(result < minInterval){
            result = minInterval;
        }

        return result;
    }
}
