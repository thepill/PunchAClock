package de.jbdevelop.punchaclock.ui.debug;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.db.WifiRepository;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;
import de.jbdevelop.punchaclock.model.Wifi;

public class DebugImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
    }

    public void importData(View view) {
        EditText textField = (EditText) findViewById(R.id.debug_import_value);

        String data = textField.getText().toString();

        importBackup(data);
    }

    public void importBackup(String backupCSV) {
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);

        Map<String, Area> areas = new HashMap<>();
        List<TimePeriod> timePeriods = new ArrayList<>();

        String[] dataSets = backupCSV.split("\n");
        for (String dataSet : dataSets) {

            try {

                String[] entries = dataSet.split(",");

                String areaName = entries[2];
                String lat = entries[3];
                String lon = entries[4];

                Area area;
                if (areas.containsKey(areaName)) {
                    area = areas.get(areaName);
                } else {
                    if (areaName.isEmpty()) {
                        continue;
                    }

                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    area = new Area(areaName, latLng, 100);
                    areas.put(area.getDescription(), area);
                }

                TimePeriod timePeriod = new TimePeriod(area);
                String inTime = entries[0];
                Date in = dateFormat.parse(inTime);

                String outTime = entries[1];
                Date out = null;
                if (!outTime.isEmpty()) {
                    out = dateFormat.parse(outTime);
                }

                timePeriod.setInTimestamp(in);
                timePeriod.setOutTimestamp(out);

                timePeriods.add(timePeriod);
            } catch (Exception e) {
                continue;
            }
        }


        if (areas.values().isEmpty() && timePeriods.isEmpty()) return;

        dropDB();
        for (Area area1 : areas.values()) {
            area1.save();
        }

        for (TimePeriod tmp : timePeriods) {
            try {
                Area ar = AreaRepository.getInstance().getAreaByDescription(tmp.getArea().getDescription());
                TimePeriod timePeriod = new TimePeriod(ar);
                timePeriod.setInTimestamp(tmp.getInTimestamp());
                timePeriod.setOutTimestamp(tmp.getOutTimestamp());
                timePeriod.save();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(this, "imported " + areas.size() + " areas and " + timePeriods.size() + " timeperiods" , Toast.LENGTH_LONG)
                .show();

    }

    private static void dropDB() {
        try {
            for (Area area : AreaRepository.getInstance().getAll()) {
                AreaRepository.getInstance().delete(area);
            }

            for (TimePeriod timePeriod : TimePeriodRepository.getInstance().getAll()) {
                TimePeriodRepository.getInstance().delete(timePeriod);
            }

            for (Wifi wifi : WifiRepository.getInstance().getAll()) {
                WifiRepository.getInstance().delete(wifi);
            }


        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

}
