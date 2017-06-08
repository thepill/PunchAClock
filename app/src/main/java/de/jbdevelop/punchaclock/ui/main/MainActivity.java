package de.jbdevelop.punchaclock.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.helper.ExportHelper;
import de.jbdevelop.punchaclock.helper.PACBackup;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.helper.SettingsHelper;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.service.PACService;
import de.jbdevelop.punchaclock.service.eventBus.area.EnteredAreaEvent;
import de.jbdevelop.punchaclock.service.eventBus.area.LeftAreaEvent;
import de.jbdevelop.punchaclock.ui.debug.DebugEventBus;
import de.jbdevelop.punchaclock.ui.debug.DebugImportActivity;
import de.jbdevelop.punchaclock.ui.debug.map.DebugMapActivity;
import de.jbdevelop.punchaclock.ui.intro.IntroActivity;
import de.jbdevelop.punchaclock.ui.map.AddAreaActivity;
import de.jbdevelop.punchaclock.ui.setting.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    static final String LOGCAT_TAG = "PAC-" + MainActivity.class.getSimpleName();

    AreaListAdapter adapter;
    MainPresenter presenter;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        boolean serviceEnabled = SettingsHelper.isServiceEnabled(this);
        if (serviceEnabled) {
            if (PACService.isStopped()) {
                startService();
            }
        }

        showIntroOnFirstStart();

        adapter = new AreaListAdapter(this, new ArrayList<Area>());
        ListView areaListView = (ListView) findViewById(R.id.area_list_view);
        areaListView.setAdapter(adapter);
        presenter = new MainPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main_option, menu);
        setDebugMenuVisibility();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_option_debug_eventbus:
                startDebugEventBus();
                break;
            case R.id.menu_main_option_debug_map:
                startDebugMap();
                break;
            case R.id.menu_main_option_debug_import:
                startDebugImport();
                break;
            case R.id.menu_main_option_share:
                startShare();
                break;
            case R.id.menu_main_option_settings:
                startSettingsActivity();
                break;
            default:
                break;
        }

        return true;
    }

    private void startDebugImport() {
        Intent debugImport = new Intent(this, DebugImportActivity.class);
        startActivity(debugImport);
    }

    private void startShare() {
        String data = ExportHelper.getCSVExport(this);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void startService() {
        final Intent pcaService = new Intent(this, PACService.class);
        new Thread("PACService Thread") {
            @Override
            public void run() {
                PACLog.i(LOGCAT_TAG, "Starting PACService");
                startService(pcaService);
            }
        }.start();
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
    }

    private boolean locationPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDebugMenuVisibility();
        EventBus.getDefault().register(this);

        boolean serviceEnabled = SettingsHelper.isServiceEnabled(this);
        setServiceDisabledHintVisibility(serviceEnabled);

        presenter.updateAreas();
        showLocationDisabledHint();
    }

    private void setDebugMenuVisibility() {
        boolean debugEnabled = SettingsHelper.isDebugEnabled(this);

        if (menu != null) {
            menu.setGroupVisible(R.id.menu_main_option_debug_group, debugEnabled);
        }
    }

    private void showLocationDisabledHint() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> enabledProviders = locationManager.getProviders(true);

        if (!enabledProviders.contains(LocationManager.GPS_PROVIDER) && !enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "Location Services disabled. Please enable for proper functionality.", Toast.LENGTH_LONG).show();
        }
    }

    private void setServiceDisabledHintVisibility(boolean visibile) {
        TextView serviceDisabledHint = (TextView) findViewById(R.id.service_disabled_hint);
        serviceDisabledHint.setVisibility(visibile ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void startDebugEventBus() {
        startActivity(new Intent(this, DebugEventBus.class));
    }

    public void addAreas(View view) {
        Intent mapsActivity = new Intent(this, AddAreaActivity.class);
        startActivity(mapsActivity);
    }

    public void startDebugMap() {
        Intent debugMap = new Intent(this, DebugMapActivity.class);
        startActivity(debugMap);
    }

    public void startSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void startBackupActivity() {
        Intent settingsIntent = new Intent(this, PACBackup.class);
        startActivity(settingsIntent);
    }


    @Subscribe
    public void onEnteredArea(EnteredAreaEvent event) {
        presenter.updateAreas();
    }

    @Subscribe
    public void onLeftArea(LeftAreaEvent event) {
        presenter.updateAreas();
    }

    private static final String INTRO_SHARED_PREF_KEY = "introNotShown";

    void showIntroOnFirstStart() {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isFirstStart = getPrefs.getBoolean(INTRO_SHARED_PREF_KEY, true);

        if (isFirstStart) {
            startActivity(new Intent(this, IntroActivity.class));
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean(INTRO_SHARED_PREF_KEY, false);
            e.apply();
        } else if(!locationPermissionsGranted()) {
            requestLocationPermissions();
        }
    }
}
