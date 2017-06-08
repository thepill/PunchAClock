package de.jbdevelop.punchaclock.service.eventBus.area;

import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by Jan on 30.03.2016.
 */
public class LeftAreaEvent {
    private Area area;

    public LeftAreaEvent(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }
}
