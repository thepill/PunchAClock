package de.jbdevelop.punchaclock.ui.setting;

import android.content.SharedPreferences;

/**
 * Created by Benjamin on 25.02.17.
 */

public class SettingChangedEvent {
    private SharedPreferences preferences;
    private String key;

    public SettingChangedEvent(SharedPreferences preferences, String key){
        this.preferences = preferences;
        this.key = key;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public String getKey(){
        return key;
    }

    public Object getValue(){
        return preferences.getAll().get(key);
    }

}
