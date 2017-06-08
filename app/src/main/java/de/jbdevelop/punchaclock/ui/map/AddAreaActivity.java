package de.jbdevelop.punchaclock.ui.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.jbdevelop.punchaclock.R;

public class AddAreaActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, SeekBar.OnSeekBarChangeListener {
    GoogleMap mMap;
    AddAreaPresenter presenter;

    final static int ZOOM_LEVEL = 16;

    @Bind(R.id.radiusSeekBar)
    SeekBar seekBar;

    @Bind(R.id.createArea)
    FloatingActionButton createArea_btn;

    @Bind(R.id.radiusSize)
    TextView radiusSizeTextView;

    @Bind(R.id.search_bar)
    EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area_add);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this);

        seekBar.setOnSeekBarChangeListener(this);
        searchBar.setOnEditorActionListener(editorActionListener);

        presenter = new AddAreaPresenter(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(locationChangeListener);

        presenter.drawExistingAreas();
    }

    public void createArea(View view) {
        if (presenter.newAreaIsVisible()) {
            presenter.createArea();
        } else {
            drawArea(mMap.getCameraPosition().target);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        drawArea(latLng);
    }

    private void drawArea(LatLng latLng) {
        createArea_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_done_24dp));
        presenter.drawArea(latLng);
        hideKeyboard();
    }

    public void searchAddress() {
        String search = searchBar.getText().toString();
        Geocoder geocoder = new Geocoder(getBaseContext());

        try {
            List<Address> addresses = geocoder.getFromLocationName(search, 1);
            boolean foundAddress = !addresses.isEmpty();
            if (foundAddress) {
                handleAddress(addresses.get(0));
            } else
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.activity_addAdrea_search_notfound)
                        + search, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    private void handleAddress(Address address) {
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        moveCamera(latLng);

        String addressText = address.getAddressLine(0) + ", " + address.getLocality();

        if (addressText != null) {
            if (address.getLocality().equals(address.getAddressLine(0))) {
                addressText = address.getLocality();
            }
            Toast.makeText(getApplicationContext(), addressText, Toast.LENGTH_SHORT).show();
        }
        searchBar.setText("");
        hideKeyboard();
    }

    private void moveCamera(LatLng coordinates) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, ZOOM_LEVEL));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        presenter.changeMarkerRadius(progress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMap != null) {
            mMap.setMyLocationEnabled(false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private GoogleMap.OnMyLocationChangeListener locationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            moveCamera(coordinates);
            mMap.setOnMyLocationChangeListener(null);
        }
    };

    public void hideKeyboard() {
        View v = this.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private EditText.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                searchAddress();
                return true;
            }
            return false;
        }
    };
}
