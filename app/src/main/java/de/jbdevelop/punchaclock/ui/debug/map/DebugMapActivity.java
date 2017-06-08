package de.jbdevelop.punchaclock.ui.debug.map;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.model.LocationDebugEntry;
import de.jbdevelop.punchaclock.model.Area;

public class DebugMapActivity extends FragmentActivity {

    int ZOOM_LEVEL = 14;

    GoogleMap mMap; // Might be null if Google Play services APK is not available.

    Map<Marker, Location> markerLocationMap = new LinkedHashMap<>();
    List<LocationDebugEntry> debugEntries;

    @Bind(R.id.seekBar)
    SeekBar seekBar;

    @Bind(R.id.avgTime)
    TextView avgTime;

    @Bind(R.id.biggestDelta)
    TextView biggestDelta;

    @Bind(R.id.checkBox)
    CheckBox showOnlyCurrent;

    @Bind(R.id.numberOfUpdates)
    TextView numberOfUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_map);
        ButterKnife.bind(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drawMarkers();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        showOnlyCurrent.setChecked(true);
        showOnlyCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                drawMarkers();
            }
        });
    }

    private void drawMarkers() {
        int i = 0;

        for (Map.Entry<Marker, Location> entry : markerLocationMap.entrySet()) {
            Marker marker = entry.getKey();
            if (seekBar.getProgress() > i) {
                marker.setVisible(!showOnlyCurrent.isChecked());
            } else if (seekBar.getProgress() == i) {
                marker.setVisible(true);
                marker.showInfoWindow();
            } else {
                marker.setVisible(false);
            }
            i++;
        }
    }

    private void setAverageTimeBetweenUpdates() {
        if(debugEntries.size() <= 1) {
            return;
        }

        LocationDebugEntry prevEntry = null;
        long timeBetween = 0;
        long all = 0;
        long biggestDelta = 0;

        for (LocationDebugEntry entry : debugEntries) {
            if(prevEntry == null) {
                prevEntry = entry;
                continue;
            }

            timeBetween = entry.getLocation().getTime() - prevEntry.getLocation().getTime();
            all += timeBetween;

            if(biggestDelta < timeBetween) {
                biggestDelta = timeBetween;
            }
            prevEntry = entry;
        }

        avgTime.setText("avg between updates: " + (all / (debugEntries.size() - 1)) / 1000 + " s");
        this.biggestDelta.setText("Biggest Delta: " + biggestDelta / 1000 + " s");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        debugEntries = LocationDebugEntry.fetchAll();
        setAverageTimeBetweenUpdates();
        seekBar.setMax(debugEntries.size() - 1);

        for(LocationDebugEntry entry : debugEntries) {
            Location location = entry.getLocation();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));

            if(location.getProvider().equals("network")) {
                marker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            markerLocationMap.put(marker, location);
        }

        mMap.setInfoWindowAdapter(new InfoWindowAdapter(this, markerLocationMap));

        numberOfUpdates.setText("Number of Updates: " + debugEntries.size());
        seekBar.setProgress(seekBar.getMax());
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(locationChangeListener);
        drawEnabledAreas();
    }

    private void drawEnabledAreas(){
        for (Area area : AreaRepository.getInstance().getAllEnabled()) {
            LatLng position = area.getCoordinates();
            mMap.addCircle(new CircleOptions().center(position).radius(area.getRadius()).fillColor(0x407AD8FF).strokeColor(0x80ffffff));
        }
    }

    private GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, ZOOM_LEVEL));
            mMap.setOnMyLocationChangeListener(null);
        }
    };
}
