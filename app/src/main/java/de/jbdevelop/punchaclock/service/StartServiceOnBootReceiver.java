package de.jbdevelop.punchaclock.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;

/**
 * Created by jan on 30.10.15.
 */

public class StartServiceOnBootReceiver extends BroadcastReceiver {
    static final String LOGCAT_TAG = "PAC-" +  StartServiceOnBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {

        if(SettingsHelper.isServiceEnabled(context)){
            final Intent pacService = new Intent(context, PACService.class);

            new Thread("PACService Thread") {
                @Override
                public void run() {
                    //Logcat tag limit = 23
                    PACLog.i(LOGCAT_TAG.substring(0,22), "Starting PACService");
                    context.startService(pacService);
                }
            }.start();
        }
    }
}

