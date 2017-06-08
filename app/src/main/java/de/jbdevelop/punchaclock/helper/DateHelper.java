package de.jbdevelop.punchaclock.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jan on 02.05.2016.
 */
public class DateHelper {
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /** if earlier is null, returns "", if later date is null, returns NOW - earlier date
     */
    public static String getDurationString(Date earlier, Date later) {
        if(earlier == null) {
            return "";
        }

        Date laterDate = later;
        if(laterDate == null) {
            laterDate = new Date();
        }

        String text = "";
        long seconds =(laterDate.getTime() - earlier.getTime() ) / 1000;

        int days = (int) seconds / (86400);
        if(days > 0)
            text += days + "d ";

        int hours = (int) (seconds % 86400) / 3600;
        if(hours > 0)
            text += hours + "h ";

        int minutes = (int) (seconds % 3600) / (60);
        text += minutes + "m";

        return text;
    }
}
