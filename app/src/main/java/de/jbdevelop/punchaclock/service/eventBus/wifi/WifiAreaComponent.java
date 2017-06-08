package de.jbdevelop.punchaclock.service.eventBus.wifi;

import android.content.Context;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.jbdevelop.punchaclock.db.WifiRepository;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.Wifi;
import de.jbdevelop.punchaclock.service.eventBus.area.EnteredAreaEvent;
import de.jbdevelop.punchaclock.service.eventBus.area.LeftAreaEvent;
import de.jbdevelop.punchaclock.service.job.WifiDetectionJob;

/**
 * Created by Jan on 30.03.2016.
 */
public class WifiAreaComponent {
    public final String LOGCAT_TAG = "PAC-" + WifiAreaComponent.class.getSimpleName();
    private final int SCHEDULE_OFFSET = 5 * 60000;
    private final int ONEMINUTE_IN_MS = 1 * 60000;

    private Context context;

    public WifiAreaComponent(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onWifiConnectedEvent(WifiConnectedEvent event) {
        if(isMonitoringWifiDisabled()){
            return;
        }

        String macOfAP = event.getWifiInfo().getBSSID();
        Wifi storedWifi = WifiRepository.getInstance().getWifiByMAC(macOfAP);

        if(storedWifi == null){
            PACLog.i(LOGCAT_TAG, "Wifi not found in database - TriggerEnterAreaEvent not fired!");
            return;
        }

        PACLog.i(LOGCAT_TAG, "Wifi found in database: ssid: " + storedWifi.getSSID() + " |  mac of ap: " + macOfAP);
        Area associatedArea = storedWifi.getArea();

        if(!associatedArea.isMonitoringWifi()){
            PACLog.i(LOGCAT_TAG, "Monitoring wifi for area " + associatedArea.getDescription() + " is disabled");
            return;
        }

        EventBus.getDefault().post(new TriggerEnterAreaEvent(associatedArea));
    }

    @Subscribe
    public void onEnteredAreaEvent(EnteredAreaEvent enteredAreaEvent){
        if(isMonitoringWifiDisabled()){
            return;
        }

        Area area = enteredAreaEvent.getArea();

        if(area.isMonitoringWifi()){
            scheduleWifiDetectionJob(area);
        }
        else {
            PACLog.i(LOGCAT_TAG, "Monitoring wifi for area " + area.getDescription() + " is disabled");
        }
    }

    private void scheduleWifiDetectionJob(Area area){
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("area-id", area.getId());
        int runFromOffset = calculateRuntimeOffset(area.getRadius());
        int runTillOffset = runFromOffset + ONEMINUTE_IN_MS;

        //job gets fired between runFrom offset and runTill
        new JobRequest.Builder(WifiDetectionJob.TAG)
                .setExecutionWindow(runFromOffset, runTillOffset)
                .setExtras(extras)
                .build()
                .schedule();
        PACLog.i(LOGCAT_TAG, "Scheduled " + WifiDetectionJob.class.getSimpleName() +  " job for area " + area.getDescription());
    }

    private int calculateRuntimeOffset(double radiusOfArea){
        int radius = (int) radiusOfArea;

        return radius/100 * SCHEDULE_OFFSET;
    }

    @Subscribe
    public void onLeftAreaEvent(LeftAreaEvent leftAreaEvent){
        PACLog.i(LOGCAT_TAG, "Removing pending WifiDetection jobs ");
        JobManager.instance().cancelAllForTag(WifiDetectionJob.TAG);
    }

    private boolean isMonitoringWifiDisabled(){
        return !SettingsHelper.isMonitoringWifiEnabled(this.context);
    }
}
