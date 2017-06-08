package de.jbdevelop.punchaclock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by Benjamin on 05.04.16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AreaRepositoryTest {
    private AreaRepository areaRepository;

    @Before
    public void prepare(){
        areaRepository = AreaRepository.getInstance();
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_ShouldThrowExceptionWhenParameterIsNull(){
        areaRepository.insert(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_ShouldThrowExceptionWhenParameterIsNull(){
        areaRepository.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_ShouldThrowExceptionWhenParameterIsNull() throws NoSuchFieldException {
        areaRepository.delete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAreaByDescprition_ShouldThrowExceptionWhenParameterIsNull() throws NoSuchFieldException {
        areaRepository.getAreaByDescription(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAreaByDescprition_ShouldThrowExceptionWhenParameterIsEmpty() throws NoSuchFieldException {
        areaRepository.getAreaByDescription("");
    }
}
