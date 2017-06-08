package de.jbdevelop.punchaclock.service.eventBus.area;

import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by Jan on 30.03.2016.
 */
public class EnteredAreaEvent {
    private Area area;

    public EnteredAreaEvent(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }
}
