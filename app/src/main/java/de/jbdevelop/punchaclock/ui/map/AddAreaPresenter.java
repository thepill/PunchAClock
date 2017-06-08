package de.jbdevelop.punchaclock.ui.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.service.eventBus.location.RequestLocationEvent;

/**
 * Created by jan on 02.11.15.
 */
public class AddAreaPresenter {
    final int MIN_RADIUS = 100;

    Context context;
    AddAreaActivity view;
    AreaMarker marker;

    public AddAreaPresenter(AddAreaActivity view) {
        this.view = view;
        this.context = view.getApplication();
    }

    public void drawExistingAreas() {
        for (Area area : AreaRepository.getInstance().getAll()) {
            new AreaMarker(area, view.mMap);
        }
    }

    public boolean newAreaIsVisible() {
        return marker != null;
    }

    public void drawArea(LatLng latLng) {
        if(marker == null) {
            view.seekBar.setVisibility(View.VISIBLE);
            view.createArea_btn.setVisibility(View.VISIBLE);
            view.radiusSizeTextView.setVisibility(View.VISIBLE);
            Area tempArea = new Area("", latLng, MIN_RADIUS);
            marker = new AreaMarker(tempArea, view.mMap);
            view.radiusSizeTextView.setText(String.valueOf(MIN_RADIUS));
        } else {
            moveMarker(latLng);
        }
    }

    public void changeMarkerRadius(int seekbarProgress) {
        int newRadius = seekbarProgress + MIN_RADIUS;
        if(marker != null) {
            marker.setRadius(newRadius);
        }
        String radiusText = newRadius + " m";
        view.radiusSizeTextView.setText(radiusText);
    }

    private void moveMarker(LatLng latLng) {
        marker.setPosition(latLng);
    }

    public void createArea() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view);
        builder.setTitle(context.getResources().getText(R.string.activity_addAdrea_area_new_enterName));

        final EditText input = new EditText(view);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = input.getText().toString();
                LatLng coordinates = marker.getPosition();
                double radius = marker.getRadius();

                Area newArea = new Area(description, coordinates, radius);
                AreaRepository.getInstance().insert(newArea);
                marker = null;

                EventBus.getDefault().post(new RequestLocationEvent());

                view.createArea_btn.setVisibility(View.INVISIBLE);
                view.seekBar.setVisibility(View.INVISIBLE);
                view.radiusSizeTextView.setVisibility(View.INVISIBLE);
                view.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}
