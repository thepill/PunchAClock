package de.jbdevelop.punchaclock.db;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by Benjamin on 31.03.16.
 */
public class TimePeriodRepository implements ITimePeriodRepository {
    private static TimePeriodRepository instance;

    private TimePeriodRepository() {}

    public static synchronized TimePeriodRepository getInstance(){
        if(instance == null){
            instance = new TimePeriodRepository();
        }
        return instance;
    }

    @Override
    public void insert(TimePeriod timePeriod) throws IllegalArgumentException {
        if(timePeriod == null){
            throw new IllegalArgumentException("argument timePeriod is null");
        }

        timePeriod.save();
    }

    @Override
    public TimePeriod get(long id) throws NoSuchFieldException {
        TimePeriod timePeriod = new Select()
                .from(TimePeriod.class)
                .where("id=?", id)
                .executeSingle();

        if(timePeriod == null){
            throw new NoSuchFieldException("timeperiod with id " + id + " not found");
        }

        return timePeriod;
    }

    @Override
    public void update(TimePeriod timePeriod) throws IllegalArgumentException {
        if(timePeriod == null){
            throw new IllegalArgumentException("argument area is null");
        }

        timePeriod.save();
    }

    @Override
    public void delete(TimePeriod timePeriod) throws IllegalArgumentException,NoSuchFieldException {
        if(timePeriod == null){
            throw new IllegalArgumentException("argument timePeriod is null");
        }

        TimePeriod storedTimePeriod = null;
        try {
            storedTimePeriod = get(timePeriod.getId());
        } catch (NoSuchFieldException e) {
            throw e;
        }

        storedTimePeriod.delete();
    }

    @Override
    public List<TimePeriod> getAll() {
        return new Select()
                .from(TimePeriod.class)
                .orderBy(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat(" DESC"))
                .execute();
    }

    @Override
    public List<TimePeriod> getAllTimeperiodsForArea(Area area) throws IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        return new Select().from(TimePeriod.class)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_FOREIGNKEY_AREA.concat("=?"), area.getId())
                .orderBy(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat(" DESC"))
                .execute();
    }

    @Override
    public TimePeriod getChronologicallyFirstTimePeriod() {
        return new Select().from(TimePeriod.class)
                .orderBy(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat(" ASC"))
                .executeSingle();
    }

    @Override
    public TimePeriod getChronologicallyLastTimePeriod() {
        return new Select().from(TimePeriod.class)
                .orderBy(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_OUT.concat(" DESC"))
                .executeSingle();
    }

    @Override
    public TimePeriod getActiveTimePeriod() {
        return new Select().from(TimePeriod.class)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_CURRENTACTIVE.concat("=?"), true)
                .executeSingle();
    }

    @Override
    public TimePeriod getActiveTimeperiodForArea(Area area) throws NoSuchFieldException, IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        TimePeriod activeTimePeriod =  new Select().from(TimePeriod.class)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_FOREIGNKEY_AREA.concat("=?"), area.getId())
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_CURRENTACTIVE.concat("=?"), true)
                .executeSingle();

        if(activeTimePeriod == null){
            throw new NoSuchFieldException("no active timeperiod for area with id " + area.getId() + " found");
        }

        return activeTimePeriod;
    }

    @Override
    public TimePeriod getLastTimeperiodForArea(Area area) throws NoSuchFieldException, IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        TimePeriod activeTimePeriod =  new Select().from(TimePeriod.class)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_FOREIGNKEY_AREA.concat("=?"), area.getId())
                .orderBy(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat(" DESC"))
                .executeSingle();

        if(activeTimePeriod == null){
            throw new NoSuchFieldException("no active timeperiod for area with id " + area.getId() + " found");
        }

        return activeTimePeriod;
    }

    @Override
    public List<TimePeriod> getByDate(Area area, long startingTimestamp) throws NoSuchFieldException, IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        if(startingTimestamp <= 0){
            throw new IllegalArgumentException("argument startinTimestamp is >= 0");
        }

        long endOfDay = startingTimestamp + TimeUnit.DAYS.toMillis(1);

        List<TimePeriod> activeTimePeriods =  new Select().from(TimePeriod.class)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat(">=?"), startingTimestamp)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat("<=?"), endOfDay)
                .where(DatabaseContract.TimePeriodContract.COLUMN_NAME_FOREIGNKEY_AREA.concat("=?"), area.getId())
                .orderBy(DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN.concat(" DESC"))
                .execute();

        if(activeTimePeriods == null){
            throw new NoSuchFieldException("no active timeperiod for area with id " + area.getId() + " found");
        }

        return activeTimePeriods;
    }


}
