package de.jbdevelop.punchaclock.service;

import android.content.Context;

/**
 * Created by Jan on 30.03.2016.
 */
public class ServiceStartupComponent {
    public final String LOGCAT_TAG = "PAC-" + ServiceStartupComponent.class.getSimpleName();

    public static void initService(Context context) {
        //WifiComponent.update(context);
    }
}
