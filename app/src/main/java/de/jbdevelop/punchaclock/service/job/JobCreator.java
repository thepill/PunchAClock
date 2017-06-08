package de.jbdevelop.punchaclock.service.job;

import com.evernote.android.job.Job;

/**
 * Created by Benjamin on 15.04.16.
 */
public class JobCreator implements com.evernote.android.job.JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag) {
            case WifiDetectionJob.TAG:
                return new WifiDetectionJob();
            case MotionDetectionJob.TAG:
                return new MotionDetectionJob();
            default:
                return null;
        }
    }
}
