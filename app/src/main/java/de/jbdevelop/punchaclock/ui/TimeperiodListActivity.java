package de.jbdevelop.punchaclock.ui;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.mobsandgeeks.adapters.Sectionizer;
import com.mobsandgeeks.adapters.SimpleSectionAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;
import de.jbdevelop.punchaclock.ui.setting.TimePeriodAdapter;

public class TimeperiodListActivity extends AppCompatActivity implements Sectionizer<TimePeriod>,OnDateSetListener {

    public static final String AREA_ID_KEY = "areaId";

    Area area;
    TimePeriodAdapter timePeriodAdapter;
    SimpleSectionAdapter<TimePeriod> sectionAdapter;
    List<TimePeriod> timePeriods;
    private ListView listView;
    private long selectedDateMillis;
    Button dateButton;
    Button dateClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeperiod_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.timeperiod_list_toolbar);
        setSupportActionBar(myToolbar);

        listView = (ListView) findViewById(R.id.timeperiod_list);
        dateButton = (Button) findViewById(R.id.timeperiod_date_button);
        dateClear = (Button) findViewById(R.id.date_clear);


        long areaId = getIntent().getExtras().getLong("areaId");
        try {
            area = AreaRepository.getInstance().get(areaId);
            setTitle(area.getDescription());

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        timePeriods = TimePeriodRepository.getInstance().getAllTimeperiodsForArea(area);
        updateTimePeriods(timePeriods);

        setListAdapter(timePeriods);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("selectedDateMillis", selectedDateMillis );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedDateMillis = savedInstanceState.getLong("selectedDateMillis");

        updateDateSelectionText(selectedDateMillis);

        if(selectedDateMillis >= 0){
            showTimePeriodsByDate(selectedDateMillis);
            dateClear.setVisibility(View.VISIBLE);
        }
    }

    private void setListAdapter(List<TimePeriod> timePeriodList) {
        if (timePeriodList.isEmpty()) {
            setAreaNotYetEnteredAdapter(listView);
        } else {
            listView.setAdapter(sectionAdapter);
        }
    }

    private void setAreaNotYetEnteredAdapter(ListView listView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{getString(R.string.activity_timeperiodList_noentries)});
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_area_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.area_action_settings:
                startSettingsActivity();
                break;
            default:
                break;
        }

        return true;
    }

    public void startSettingsActivity() {
        Intent areaSettings = new Intent(this, AreaSettingsActivity.class);
        areaSettings.putExtra(AREA_ID_KEY, area.getId());
        startActivity(areaSettings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTimePeriods(timePeriods);
    }

    private void updateTimePeriods(List<TimePeriod> timePeriodList) {
        timePeriodAdapter = new TimePeriodAdapter(this, new ArrayList<>(timePeriodList));
        sectionAdapter = new SimpleSectionAdapter<>(this, timePeriodAdapter, R.layout.list_header, R.id.timeperiod_list_header, this);

        setListAdapter(timePeriodList);
    }


    @Override
    public String getSectionTitleForItem(TimePeriod instance) {
        Date in = instance.getInTimestamp();
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY;
        String formatted = DateUtils.formatDateTime(this, in.getTime(), flags);
        return formatted;
    }

    public void showDatePickerDialog(View view){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateClear.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        selectedDateMillis = cal.getTimeInMillis();

        updateDateSelectionText(selectedDateMillis);
        showTimePeriodsByDate(selectedDateMillis);
    }

    public void showTimePeriodsByDate(long timestampOfDay){
        try {
            timePeriods = TimePeriodRepository.getInstance().getByDate(area, timestampOfDay);
            updateTimePeriods(timePeriods);
        } catch (Exception e) {
            PACLog.e("Error",e.getMessage());
            return;
        }
    }

    private void updateDateSelectionText(long dateMillis){
        if(dateMillis <= 0 ){
            dateButton.setText(getString(R.string.activity_timeperiodList_datefilter_default));
            return;
        }

        int flags = DateUtils.FORMAT_SHOW_DATE;
        String formattedDate = DateUtils.formatDateTime(this, dateMillis, flags);
        dateButton.setText(" " + formattedDate);
    }

    public void clearDateSelection(View view){
        dateClear.setVisibility(View.INVISIBLE);
        selectedDateMillis = -1;
        dateButton.setText(getString(R.string.activity_timeperiodList_datefilter_default));
        timePeriods = TimePeriodRepository.getInstance().getAllTimeperiodsForArea(area);
        updateTimePeriods(timePeriods);
    }
}
