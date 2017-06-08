package de.jbdevelop.punchaclock.ui.setting;

import android.content.Context;
import android.util.AttributeSet;

import de.jbdevelop.punchaclock.R;

/**
 * Created by Benjamin on 07.01.17.
 */

public class MotionSensorIntervalPreference extends SeekbarPreference {

    public MotionSensorIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        DEFAULT_VALUE = 60;
        MIN_VALUE = 10;
        MAX_VALUE = 200;
        STEPSIZE = 10;
        UNIT = "s";
        DETAIL_TEXT = context.getResources().getString(R.string.setting_motionMinInterval_detail);
    }
}
