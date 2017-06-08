package de.jbdevelop.punchaclock;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import de.jbdevelop.punchaclock.helper.Haversine;
import de.jbdevelop.punchaclock.helper.MotionHelper;
import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by Benjamin on 31.03.16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class HelperTest {

    @Test
    public void getHaversineInMeters_ShouldReturnCorrectValueForLatLng(){
        double value = Haversine.getHaversineInMeters(new LatLng(20,20), new LatLng(30,30));

        Assert.assertEquals(value, 1499522.7658503223);
    }

    @Test
    public void getHaversineInMeters_ShouldReturnCorrectValueForLocation(){
        Location locationFrom = new Location("gps");
        locationFrom.setLongitude(20);
        locationFrom.setLatitude(20);
        Location locationTo = new Location("gps");
        locationTo.setLongitude(30);
        locationTo.setLatitude(30);

        double value = Haversine.getHaversineInMeters(locationFrom,locationTo);

        Assert.assertEquals(value, 1499522.7658503223);
    }

    @Test
    public void getHaversineInMeters_ShouldReturnCorrectValueForLocationAndArea(){
        Location locationFrom = new Location("gps");
        locationFrom.setLongitude(20);
        locationFrom.setLatitude(20);
        Area area = new Area("Test", new LatLng(30,30), 100);

        double value = Haversine.getHaversineInMeters(locationFrom,area);

        Assert.assertEquals(value, 1499522.7658503223);
    }

    @Test
    public void getHaversineInMeters_ShouldReturnCorrectValueForAreas(){
        Area areaFrom = new Area("Test", new LatLng(20,20), 100);
        Area areaTo = new Area("Test", new LatLng(30,30), 100);

        double value = Haversine.getHaversineInMeters(areaFrom,areaTo);

        Assert.assertEquals(value, 1499522.7658503223);
    }

    @Test
    public void getNextRuntimeInMinutes_ShouldReturnCorrectValueForNextRun(){
        double distanceToNextArea = 10_000;     //meter
        double velocity = 30;                   // meter per second

        long result = MotionHelper.getNextRuntimeInSeconds(RuntimeEnvironment.application, distanceToNextArea, velocity);

        Assert.assertEquals(333,result);
    }

    @Test
    public void getNextRuntimeInMinutes_ShouldReturnCorrectValueFor0Velocity(){
        double distanceToNextArea = 10_000;     //meter
        double velocity = 0;                   // meter per second

        long result = MotionHelper.getNextRuntimeInSeconds(RuntimeEnvironment.application, distanceToNextArea, velocity);

        Assert.assertEquals(250,result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNextRuntimeInMinutes_ShouldThrowExceptionForIllegalArgument(){
        double distanceToNextArea = -1;     //meter
            double velocity = -2;           // meter per second

        long result = MotionHelper.getNextRuntimeInSeconds(RuntimeEnvironment.application, distanceToNextArea, velocity);

        Assert.assertEquals(50,result);
    }

    @Test
    public void getNextRuntimeInMinutes_ShouldReturnCorrectValueForLowerThanMinRuntime(){
        double distanceToNextArea = 1;     //meter
        double velocity = 2;               // meter per second

        long result = MotionHelper.getNextRuntimeInSeconds(RuntimeEnvironment.application, distanceToNextArea, velocity);

        Assert.assertEquals(60,result);
    }
}
