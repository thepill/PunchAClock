package de.jbdevelop.punchaclock.db;

import java.util.Date;
import java.util.List;

import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by Benjamin on 31.03.16.
 */
public interface ITimePeriodRepository {
    void insert(TimePeriod timePeriod) throws IllegalArgumentException;
    TimePeriod get(long id) throws NoSuchFieldException;
    void update(TimePeriod timePeriod) throws IllegalArgumentException;
    void delete(TimePeriod timePeriod) throws NoSuchFieldException;
    List<TimePeriod> getAll();
    List<TimePeriod> getAllTimeperiodsForArea(Area area) throws IllegalArgumentException;
    TimePeriod getActiveTimeperiodForArea(Area area) throws NoSuchFieldException, IllegalArgumentException;
    TimePeriod getChronologicallyFirstTimePeriod();
    TimePeriod getChronologicallyLastTimePeriod();
    TimePeriod getActiveTimePeriod();
    TimePeriod getLastTimeperiodForArea(Area area) throws NoSuchFieldException, IllegalArgumentException;
    List<TimePeriod> getByDate(Area area, long startingTimestamp) throws NoSuchFieldException, IllegalArgumentException;
}
