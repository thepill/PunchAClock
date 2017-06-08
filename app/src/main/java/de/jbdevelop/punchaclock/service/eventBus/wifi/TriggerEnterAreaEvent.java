package de.jbdevelop.punchaclock.service.eventBus.wifi;

import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by Jan on 30.03.2016.
 */
public class TriggerEnterAreaEvent {
    private Area area;

    public TriggerEnterAreaEvent(Area area) {
        this.area = area;
    }

    public Area getArea() {
        return area;
    }
}
