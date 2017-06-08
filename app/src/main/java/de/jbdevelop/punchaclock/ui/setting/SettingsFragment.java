package de.jbdevelop.punchaclock.ui.setting;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;

import static de.jbdevelop.punchaclock.ui.setting.SettingsActivity.KEY_CALENDAR_SYNC;
import static de.jbdevelop.punchaclock.ui.setting.SettingsActivity.KEY_VERSION;

/**
 * Created by Benjamin on 19.04.16.
 */
public class SettingsFragment extends PreferenceFragment {
    public static final int DEBUG_CLICKS_TILL_TOGGLE = 6;
    public static final int DEBUG_CLICKS_TILL_TOAST = 3;

    private int debugCounter = 0;
    private Toast debugToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        debugToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        Preference versionPref = findPreference(KEY_VERSION);
        versionPref.setSummary(getAppVersion());

        versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {;
                onVersionClick(preference);
                return true;
            }
        });
    }

    private String getAppVersion(){
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException exc){
            return "";
        }
    }

    private void onVersionClick(Preference pref){
        debugCounter++;

        if(debugCounter >= DEBUG_CLICKS_TILL_TOAST && debugCounter < DEBUG_CLICKS_TILL_TOGGLE){
            int timesTillToggle = DEBUG_CLICKS_TILL_TOGGLE - debugCounter;
            String toastText = timesTillToggle + " click/s till toggle of debug mode";
            debugToast.setText(toastText);
            debugToast.show();
        }

        if(debugCounter == DEBUG_CLICKS_TILL_TOGGLE){
            debugCounter = 0;
            boolean debugEnabled = SettingsHelper.toggleDebug(pref.getContext());
            showDebugToggleToast(debugEnabled);
            return;
        }

    }

    private void showDebugToggleToast(boolean debugEnabled){
        String toastText = "";

        if(debugEnabled) {
            toastText = "Enabled debug mode";
        }else{
            toastText = "Disabled debug mode";
        }

        debugToast.setText(toastText);
        debugToast.show();
    }
}
