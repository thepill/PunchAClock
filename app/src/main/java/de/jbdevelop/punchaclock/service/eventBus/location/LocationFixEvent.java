package de.jbdevelop.punchaclock.service.eventBus.location;

import android.location.Location;

/**
 * Created by Jan on 30.03.2016.
 */
public class LocationFixEvent {
    private Location location;

    public LocationFixEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
