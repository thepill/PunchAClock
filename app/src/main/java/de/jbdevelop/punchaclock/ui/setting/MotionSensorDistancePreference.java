package de.jbdevelop.punchaclock.ui.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import de.jbdevelop.punchaclock.R;

/**
 * Created by Benjamin on 07.01.17.
 */

public class MotionSensorDistancePreference extends SeekbarPreference {
    public MotionSensorDistancePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_VALUE = 1000;
        MIN_VALUE = 100;
        MAX_VALUE = 5000;
        STEPSIZE = 100;
        UNIT = "m";
        DETAIL_TEXT = context.getResources().getString(R.string.setting_motionMinDistance_detail);
    }


}
