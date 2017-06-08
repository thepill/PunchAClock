package de.jbdevelop.punchaclock.helper;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.model.Area;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by benjamin on 30.03.17.
 */

public class ExportHelper {

    public static String getCSVExport(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);

        StringBuilder exportEntries = new StringBuilder();
        List<TimePeriod> timePeriods = TimePeriodRepository.getInstance().getAll();

        exportEntries.append("Enter,Leave,LocationName,Latitude,Longitude\n");

        for (TimePeriod timePeriod : timePeriods) {
            Area area = timePeriod.getArea();

            String inTime =  timePeriod.getInTimestamp() == null ? "" :  dateFormat.format(timePeriod.getInTimestamp());
            String outTime = timePeriod.getOutTimestamp() == null ? "" :  dateFormat.format(timePeriod.getOutTimestamp());

            String entry = inTime + "," + outTime + "," + area.toString()
                    + "," + area.getCoordinates().latitude + "," + area.getCoordinates().longitude
                    + "\n";

            exportEntries.append(entry);
        }

        String csv = exportEntries.toString();
        return csv;
    }
}
