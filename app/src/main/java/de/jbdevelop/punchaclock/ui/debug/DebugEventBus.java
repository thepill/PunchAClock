package de.jbdevelop.punchaclock.ui.debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.service.eventBus.area.EnteredAreaEvent;
import de.jbdevelop.punchaclock.service.eventBus.area.LeftAreaEvent;
import de.jbdevelop.punchaclock.service.eventBus.motion.MovementDetectedEvent;

public class DebugEventBus extends Activity {
    public final String LOGCAT_TAG = "PAC-" + DebugEventBus.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_eventbus);
    }

    public void postMovementDetected(View view) {
        PACLog.i(LOGCAT_TAG, "MANUAL-Posting MovementDetected to Event Bus");
        EventBus.getDefault().post(new MovementDetectedEvent());
    }

    public void postEnteredArea(View view){
        PACLog.i(LOGCAT_TAG, "MANUAL-Posting EnteredArea to Event Bus");
        EventBus.getDefault().post(new EnteredAreaEvent(AreaRepository.getInstance().getAll().get(0)));
    }

    public void postLeftArea(View view){
        PACLog.i(LOGCAT_TAG, "MANUAL-Posting LeftArea to Event Bus");
        EventBus.getDefault().post(new LeftAreaEvent(AreaRepository.getInstance().getAll().get(0)));
    }
}
