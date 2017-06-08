package de.jbdevelop.punchaclock;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by Benjamin on 31.03.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimePeriodTest {
    Area area;

    @Before
    public void prepare(){
        area = new Area("Testarea",new LatLng(20,20), 200);
    }
    @Test
    public void stop_ShouldNotBeActiveAndTimestampOutSet(){
        TimePeriod timePeriod = new TimePeriod(area);

        timePeriod.stop();

        Assert.assertNotNull(timePeriod.getOutTimestamp());
        Assert.assertFalse(timePeriod.isCurrentActive());
    }
}
