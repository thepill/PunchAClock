package de.jbdevelop.punchaclock.db;

import java.util.Enumeration;
import java.util.List;

import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by Benjamin on 30.03.16.
 */
public interface IAreaRepository {
    void insert(Area area) throws IllegalArgumentException;
    Area get(long id) throws NoSuchFieldException;
    void update(Area area) throws IllegalArgumentException;
    void delete(Area area) throws NoSuchFieldException, IllegalArgumentException;

    List<Area> getAll();
    List<Area> getAllEnabled();
    Area getActive() throws NoSuchFieldException;
    Area getAreaByDescription(String description) throws IllegalArgumentException, NoSuchFieldException;
}
