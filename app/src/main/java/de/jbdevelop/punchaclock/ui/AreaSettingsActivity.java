package de.jbdevelop.punchaclock.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.helper.AreaHelper;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.ui.main.MainActivity;

public class AreaSettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private final String LOGCAT_TAG = ("PAC-" + AreaSettingsActivity.class.getSimpleName()).substring(0,22);

    Area area;

    EditText nameEditText;
    TextView radiusLabel;
    SeekBar radiusSeekbar;
    Switch monitorWifi;
    Switch enabledSwitch;
    TextView radiusSeekbarValue;


    public static final int MIN_RADIUS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_settings);

        area = AreaHelper.getAreaFromIntent(getIntent());

        initDescriptionField();
        initRadiusField();
        initMonitorWifiField();
        initAreaEnabledField();
        setInputState(area.isEnabled());
    }

    public void save(View view) {
        if(area == null) {
            return;
        }

        String description = nameEditText.getText().toString();
        description = description.replace("\n", "");
        area.setDescription(description);
        area.setRadius(getRadiusValueFromSeekbar());
        area.setMonitorWifi(monitorWifi.isChecked());
        area.setEnabled(enabledSwitch.isChecked());

        AreaRepository.getInstance().update(area);
        finish();
    }

    private void initDescriptionField() {
        nameEditText = (EditText) findViewById(R.id.area_name);
        nameEditText.setText(area.getDescription());
    }

    private void initRadiusField() {
        radiusLabel = (TextView) findViewById(R.id.area_setting_radius_label);
        radiusSeekbar = (SeekBar) findViewById(R.id.area_setting_radius);
        radiusSeekbar.setOnSeekBarChangeListener(this);
        radiusSeekbarValue = (TextView) findViewById(R.id.area_setting_radius_value);

        setRadiusValueOnSeekbar(area.getRadius());
        setRadiusText(area.getRadius());
    }

    private void initMonitorWifiField(){
        monitorWifi = (Switch) findViewById(R.id.area_setting_wifi);
        monitorWifi.setChecked(area.isMonitoringWifi());
    }

    private void initAreaEnabledField() {
        enabledSwitch = (Switch) findViewById(R.id.area_setting_enabled);
        enabledSwitch.setChecked(area.isEnabled());

        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setInputState(isChecked);
            }
        });
    }

    private void setInputState(boolean state){
        radiusSeekbar.setEnabled(state);
        monitorWifi.setEnabled(state);
    }

    private void setRadiusText(double radius) {
        radiusSeekbarValue.setText(String.valueOf((int)radius) + " m");
    }

    private int getRadiusValueFromSeekbar() {
        return radiusSeekbar.getProgress() + MIN_RADIUS;
    }

    private void setRadiusValueOnSeekbar(double radius) {
        radiusSeekbar.setProgress((int)radius - MIN_RADIUS);
    }

    public void deleteArea(View view) {
        final Context context = this;

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.activity_areaSettings_action_delete_confirmTitle))
                .setMessage(String.format(getString(R.string.activity_areaSettings_action_delete_confirmHint), area.getDescription()))
                .setPositiveButton(getString(R.string.activity_areaSettings_action_delete_confirmButton_positiv), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            AreaRepository.getInstance().delete(area);
                        } catch (NoSuchFieldException e) {
                            PACLog.e(LOGCAT_TAG, e.toString());
                        }

                        // when deleting, do not go back to timeperiod view, since the area no longer exists
                        Intent startAreaList = new Intent(context, MainActivity.class);
                        startAreaList.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(startAreaList);
                    }
                })
                .setNegativeButton(getString(R.string.activity_areaSettings_action_delete_confirmButton_negativ), null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int radius = getRadiusValueFromSeekbar();
        setRadiusText(radius);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
