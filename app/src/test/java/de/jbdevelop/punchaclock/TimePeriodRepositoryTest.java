package de.jbdevelop.punchaclock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.jbdevelop.punchaclock.db.TimePeriodRepository;

/**
 * Created by Benjamin on 05.04.16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TimePeriodRepositoryTest {
    TimePeriodRepository timePeriodRepository;

    @Before
    public void prepare(){
        timePeriodRepository = TimePeriodRepository.getInstance();
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_ShouldThrowExceptionWhenParameterIsNull(){
        timePeriodRepository.insert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_ShouldThrowExceptionWhenParameterIsNull(){
        timePeriodRepository.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_ShouldThrowExceptionWhenParameterIsNull() throws NoSuchFieldException {
        timePeriodRepository.delete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAllTimeperiodsForArea_ShouldThrowExceptionWhenParameterIsNull() {
        timePeriodRepository.getAllTimeperiodsForArea(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getActiveTimeperiodsForArea_ShouldThrowExceptionWhenParameterIsNull() throws NoSuchFieldException {
        timePeriodRepository.getActiveTimeperiodForArea(null);
    }
}
