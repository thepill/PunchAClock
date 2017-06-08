package de.jbdevelop.punchaclock.service.eventBus.calendar;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.service.PACCalendar;
import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.model.TimePeriod;
import de.jbdevelop.punchaclock.service.eventBus.area.EnteredAreaEvent;
import de.jbdevelop.punchaclock.service.eventBus.area.LeftAreaEvent;

/**
 * Created by Benjamin on 26.04.16.
 */
public class CalendarSyncComponent {
    public final String LOGCAT_TAG = "PAC-" + CalendarSyncComponent.class.getSimpleName();
    private final TimePeriodRepository timePeriods = TimePeriodRepository.getInstance();

    private PACCalendar calendar;
    private Context context;

    public CalendarSyncComponent(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
        calendar = new PACCalendar(context);
        calendar.build();
    }

    @Subscribe
    public void onLeftAreaEvent(LeftAreaEvent leftAreaEvent) throws NoSuchFieldException {
        if(isSyncDisabled()){
            return;
        }

        TimePeriod timePeriod = timePeriods.getChronologicallyLastTimePeriod();
        calendar.addTimePeriod(timePeriod);
        PACLog.i(LOGCAT_TAG, "Added event | area: " + leftAreaEvent.getArea().getDescription() + "| timeperiod id: " +timePeriod.getId());
    }

    private boolean isSyncDisabled(){
        return !SettingsHelper.isCalendarSyncEnabled(this.context);
    }
}
