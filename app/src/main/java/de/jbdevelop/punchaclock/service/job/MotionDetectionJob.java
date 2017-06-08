package de.jbdevelop.punchaclock.service.job;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.greenrobot.eventbus.EventBus;

import de.jbdevelop.punchaclock.helper.AreaHelper;
import de.jbdevelop.punchaclock.helper.MotionHelper;
import de.jbdevelop.punchaclock.service.eventBus.location.LocationFixEvent;
import de.jbdevelop.punchaclock.service.eventBus.motion.StartMotionDetectionEvent;

/**
 * Created by Benjamin on 19.04.16.
 */
public class MotionDetectionJob extends Job {
    public static final String TAG = "PAC-MotionDetectionJob";
    public static final String LOGCAT_TAG = "PAC-" + MotionDetectionJob.class.getSimpleName();

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        EventBus.getDefault().post(new StartMotionDetectionEvent());
        return Result.SUCCESS;
    }
}
