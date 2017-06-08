package de.jbdevelop.punchaclock.db;

import com.activeandroid.query.Select;

import java.util.List;

import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;
import de.jbdevelop.punchaclock.model.Wifi;

/**
 * Created by Benjamin on 30.03.16.
 */
public class AreaRepository implements IAreaRepository {
    private static AreaRepository instance;
    private final WifiRepository wifiRep = WifiRepository.getInstance();
    private TimePeriodRepository timePeriodRep = TimePeriodRepository.getInstance();

    private AreaRepository() {}

    public static synchronized AreaRepository getInstance(){
        if(instance == null){
            instance = new AreaRepository();
        }

        return instance;
    }

    @Override
    public void insert(Area area) throws IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        area.save();
    }

    @Override
    public Area get(long id) throws NoSuchFieldException {
        Area area = new Select()
                .from(Area.class)
                .where("id=?", id)
                .executeSingle();

        if(area == null){
            throw new NoSuchFieldException("area with id " + id + " not found");
        }

        return area;
    }

    @Override
    public void update(Area area) throws IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        area.save();
    }

    @Override
    public void delete(Area area) throws NoSuchFieldException, IllegalArgumentException {
        if(area == null){
            throw new IllegalArgumentException("argument area is null");
        }

        Area storedArea = null;
        try {
            storedArea = get(area.getId());

            List<TimePeriod> allTimeperiodsForArea = timePeriodRep.getAllTimeperiodsForArea(area);
            for (TimePeriod timePeriod : allTimeperiodsForArea) {
                timePeriodRep.delete(timePeriod);
            }

            List<Wifi> wifis = wifiRep.getAllWifiForArea(area);
            for (Wifi wifi : wifis) {
                wifiRep.delete(wifi);
            }

        } catch (NoSuchFieldException e) {
            throw e;
        }
        storedArea.delete();
    }

    @Override
    public List<Area> getAll() {
        return new Select()
                .from(Area.class)
                .orderBy(DatabaseContract.AreaContract.COLUMN_NAME_DESCRIPTION)
                .execute();
    }

    @Override
    public List<Area> getAllEnabled() {
        return new Select()
                .from(Area.class)
                .where(DatabaseContract.AreaContract.COLUMN_NAME_ENABLED.concat("=?"), true)
                .orderBy(DatabaseContract.AreaContract.COLUMN_NAME_DESCRIPTION)
                .execute();
    }

    @Override
    public Area getActive() throws NoSuchFieldException {
        Area area =  new Select()
                .from(Area.class)
                .where(DatabaseContract.AreaContract.COLUMN_NAME_CURRENTACTIVE.concat("=?"), true)
                .executeSingle();

        if(area == null){
            throw new NoSuchFieldException("no active area found");
        }

        return area;
    }

    @Override
    public Area getAreaByDescription(String description) throws IllegalArgumentException, NoSuchFieldException {
        if(description == null || description.isEmpty()){
            throw new IllegalArgumentException("argument description was empty");
        }

        Area area = new Select()
                .from(Area.class)
                .where(DatabaseContract.AreaContract.COLUMN_NAME_DESCRIPTION.concat("=?"), description)
                .executeSingle();

        if(area == null){
            throw new NoSuchFieldException("area with description " + description + " not found");
        }

        return area;
    }
}
