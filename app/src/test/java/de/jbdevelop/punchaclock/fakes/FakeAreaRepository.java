package de.jbdevelop.punchaclock.fakes;

import java.util.ArrayList;
import java.util.List;

import de.jbdevelop.punchaclock.db.IAreaRepository;
import de.jbdevelop.punchaclock.model.Area;

public class FakeAreaRepository implements IAreaRepository{

    private  List<Area> areas;

    public FakeAreaRepository(List<Area> areas) {
        this.areas = areas;
    }

    @Override
    public void insert(Area area) {
        areas.add(area);
    }

    @Override
    public Area get(long id) {
        Area storedArea = null;

        for (Area area : areas) {
            if(area.getId() == id){
                storedArea = area;
            }
        }

        return storedArea;
    }

    @Override
    public void update(Area area) {
        return;
    }

    @Override
    public void delete(Area area) {
        if(area == null){
            return;
        }

        areas.remove(area);
    }

    @Override
    public List<Area> getAll() {
        return areas;
    }

    @Override
    public List<Area> getAllEnabled() {
        List<Area> enabledAres = new ArrayList<>();

        for (Area area : areas) {
            if(area.isEnabled()){
                enabledAres.add(area);
            }
        }

        return enabledAres;
    }

    @Override
    public Area getActive() {
        Area activeArea = null;

        for (Area area : areas) {
            if (area.isCurrentActive()) {
                activeArea = area;
            }
        }

        return activeArea;
    }

    @Override
    public Area getAreaByDescription(String description) {
        return null;
    }
}