package de.jbdevelop.punchaclock;

import android.app.Application;
import android.location.Location;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.fakes.FakeAreaRepository;
import de.jbdevelop.punchaclock.model.Area;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AreaTest extends Application {
    Location location;
    FakeAreaRepository repository;


    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

    }

    @Before
    public void prepare(){
        location = new Location("gps");
        prepareFakeRepository();
    }

   @Test
    public void IsLocationInArea_ShouldReturnFalseWhenNot(){
       Area area = repository.getAll().get(0);

       location.setLatitude(area.getCoordinates().latitude + 5);
       location.setLongitude(area.getCoordinates().longitude + 5);

       Assert.assertFalse(area.isLocationInArea(location));
   }

    @Test
    public void IsLocationInArea_ShouldReturnTrueWhenItIsInRadius(){
        Area area = repository.getAll().get(0);

        location.setLatitude(area.getCoordinates().latitude - 0.0001);
        location.setLongitude(area.getCoordinates().longitude + 0.0001);

        Assert.assertTrue(area.isLocationInArea(location));
    }

    @Test
    public void getClosestAreaFromCurrentPosition_ShouldReturnOneArea(){
        location.setLatitude(20);
        location.setLongitude(20);

        Area area = Area.getClosestAreaFromCurrentPosition(repository,location);

        Assert.assertNotNull(area);
    }

    @Test
    public void getClosestAreaFromCurrentPosition_ShouldReturnCorrectArea(){
        location.setLatitude(24);
        location.setLongitude(24);

        Area area = Area.getClosestAreaFromCurrentPosition(repository,location);

        Assert.assertEquals(area.getCoordinates().latitude, 25.0);
        Assert.assertEquals(area.getCoordinates().longitude, 25.0);
    }

    private void prepareFakeRepository(){
        List<Area> areas = new ArrayList<>();

        for(int i = 5; i <= 25; i += 5){
            areas.add(new Area("Area" + i, new LatLng(i,i), 100));
        }

        repository = new FakeAreaRepository(areas);
    }
}
