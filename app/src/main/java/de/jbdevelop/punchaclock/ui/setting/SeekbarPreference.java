package de.jbdevelop.punchaclock.ui.setting;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import de.jbdevelop.punchaclock.R;

/**
 * Created by Benjamin on 19.04.16.
 */
public class SeekbarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener{
    protected int DEFAULT_VALUE = 1000;
    protected int MIN_VALUE = 500;
    protected int MAX_VALUE = 5000;
    protected int STEPSIZE = 100;
    protected String UNIT = "";
    protected String DETAIL_TEXT = "";
    protected int value;

    private SeekBar seekBar;
    protected TextView textView;
    protected TextView detailText;

    public SeekbarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.setting_seekbar);
    }

    @Override
    protected void onBindDialogView(View view) {
        seekBar = (SeekBar) view.findViewById(R.id.setting_seekbar_seekbar);
        textView = (TextView) view.findViewById(R.id.setting_seekbar_label_value);
        detailText = (TextView) view.findViewById(R.id.setting_details_text);

        detailText.setText(DETAIL_TEXT);

        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(value - MIN_VALUE);
        seekBar.setMax(MAX_VALUE - MIN_VALUE);

        super.onBindDialogView(view);
        updateTextview();
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        if(restorePersistedValue)
        {
            value = this.getPersistedInt(DEFAULT_VALUE);
        }

    }

    @Override
    protected void onClick() {
        super.onClick();
        value = this.getPersistedInt(DEFAULT_VALUE);
        seekBar.setProgress(value - MIN_VALUE);
        updateTextview();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult)
        {
            persistInt(value);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = (progress / STEPSIZE) * STEPSIZE;
        value = progress + MIN_VALUE;
        updateTextview();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    protected void updateTextview(){
        textView.setText(String.valueOf(value) + " " + UNIT);
    }
}
