package de.jbdevelop.punchaclock.service.eventBus.area;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.helper.AreaHelper;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;
import de.jbdevelop.punchaclock.service.eventBus.location.LocationFixEvent;
import de.jbdevelop.punchaclock.service.eventBus.wifi.TriggerEnterAreaEvent;

/**
 * Created by Jan on 30.03.2016.
 */
public class AreaComponent {
    public final String LOGCAT_TAG = "PAC-" + AreaComponent.class.getSimpleName();

    private static final AreaRepository areas =  AreaRepository.getInstance();
    private static final TimePeriodRepository timePeriods = TimePeriodRepository.getInstance();

    // after leaving an area it stays "hot" for AREA_HOTNESS_PERIOD ms  . When entering the area again during this period of time
    // it's like the area was never left (the previously created timePeriod will be reused)
    public static final long AREA_HOTNESS_PERIOD = 5 * 1000 * 60;

    public AreaComponent() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onTriggerEnterAreaEvent(TriggerEnterAreaEvent triggerEnterAreaEvent) {
        PACLog.i(LOGCAT_TAG, "Received TriggerEnterArea event");
        enterArea(triggerEnterAreaEvent.getArea());
    }

    @Subscribe
    public void onGoodLocationFixEvent(LocationFixEvent locationFix) {
        PACLog.i(LOGCAT_TAG, "Received LocationFix event");
        Area area = AreaHelper.getAreaFromLocation(locationFix.getLocation());

        if(area != null) {
            enterArea(area);
        } else {
            leaveArea();
        }
    }

    private void enterArea(Area area) {
        try {
            Area active = areas.getActive();
            if(area.equals(active)) {
                PACLog.i(LOGCAT_TAG, "Already in area " + area);
                return;
            } else if(isAreaStillHot(area)) {
                reEnterHotArea(area);
                return;
            } else {
                leaveArea();
            }
        } catch (NoSuchFieldException e) {
            PACLog.e(LOGCAT_TAG, e.toString());
        }


        area.setCurrentActive(true);
        TimePeriod timePeriod = new TimePeriod(area);
        timePeriods.insert(timePeriod);
        areas.update(area);

        PACLog.i(LOGCAT_TAG, "Entered area " + area);
        postEnteredAreaEvent(area);
    }

    private boolean isAreaStillHot(Area area) {
        TimePeriod lastTimePeriod = timePeriods.getChronologicallyLastTimePeriod();
        if(lastTimePeriod == null || lastTimePeriod.getArea() != area || lastTimePeriod.getOutTimestamp() == null) return false;

        Date hotnessExpirationDate = new Date(lastTimePeriod.getOutTimestamp().getTime() + AREA_HOTNESS_PERIOD);
        Date now = new Date();
        return now.before(hotnessExpirationDate);
    }

    private void reEnterHotArea(Area area) {
        TimePeriod lastTimePeriod = timePeriods.getChronologicallyLastTimePeriod();
        lastTimePeriod.setOutTimestamp(null);
        lastTimePeriod.setCurrentActive(true);
        timePeriods.update(lastTimePeriod);
        area.setCurrentActive(true);
        areas.update(area);
    }

    private void leaveArea() {
        try {
            Area area = areas.getActive();

            TimePeriod timePeriod = timePeriods.getActiveTimeperiodForArea(area);
            timePeriod.stop();
            timePeriods.update(timePeriod);
            area.setCurrentActive(false);
            areas.update(area);

            PACLog.i(LOGCAT_TAG, "Left area " + area);
            postLeftAreaEvent(area);
        } catch (NoSuchFieldException e) {
            PACLog.e(LOGCAT_TAG, e.toString());
        }
    }



    private void postEnteredAreaEvent(Area area) {
        EventBus.getDefault().post(new EnteredAreaEvent(area));
    }

    private void postLeftAreaEvent(Area area) {
        EventBus.getDefault().post(new LeftAreaEvent(area));
    }
}
