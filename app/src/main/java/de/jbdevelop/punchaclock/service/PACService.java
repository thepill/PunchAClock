package de.jbdevelop.punchaclock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.evernote.android.job.JobManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.service.eventBus.area.AreaComponent;
import de.jbdevelop.punchaclock.service.eventBus.calendar.CalendarSyncComponent;
import de.jbdevelop.punchaclock.service.eventBus.location.PassiveLocationComponent;
import de.jbdevelop.punchaclock.service.eventBus.wifi.WifiAreaComponent;
import de.jbdevelop.punchaclock.service.eventBus.location.LocationComponent;
import de.jbdevelop.punchaclock.service.eventBus.motion.MotionComponent;
import de.jbdevelop.punchaclock.service.eventBus.wifi.WifiComponent;
import de.jbdevelop.punchaclock.service.job.JobCreator;

/**
 * Created by jan on 30.10.15.
 */
public class PACService extends Service {
    static final String LOGCAT_TAG = "PAC-" + PACService.class.getSimpleName();
    static boolean running = false;

    MotionComponent motionComponent;
    AreaComponent areaComponent;
    LocationComponent locationComponent;
    PassiveLocationComponent passiveLocationComponent;
    WifiComponent wifiComponent;
    WifiAreaComponent wifiAreaComponent;
    CalendarSyncComponent calendarSyncComponent;

    @Override
    public void onCreate() {
        PACLog.i(LOGCAT_TAG, "Service started");

        ActiveAndroid.initialize(this);
        JobManager.create(this).addJobCreator(new JobCreator());
        EventBus.getDefault().register(this);

        motionComponent = new MotionComponent(this);
        wifiComponent = new WifiComponent(this);
        areaComponent = new AreaComponent();
        locationComponent = new LocationComponent(this);
        passiveLocationComponent =  new PassiveLocationComponent(this);
        wifiAreaComponent = new WifiAreaComponent(this);
        calendarSyncComponent = new CalendarSyncComponent(this);

        ServiceStartupComponent.initService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
        JobManager.instance().cancelAll();
        super.onDestroy();
        PACLog.i(LOGCAT_TAG, "Service destroyed");
    }

    @Subscribe
    public void onStopServiceEvent(StopServiceEvent event){
        //service needs to stop himself. Otherwise android will recreate it because of START_STICKY.
        stopSelf();
    }

    public static boolean isStopped(){
        return !running;
    }
}
