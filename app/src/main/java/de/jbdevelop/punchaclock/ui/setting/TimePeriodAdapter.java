package de.jbdevelop.punchaclock.ui.setting;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.helper.DateHelper;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by Jan on 05.05.2016.
 */
public class TimePeriodAdapter extends ArrayAdapter<TimePeriod> {
    Context context;
    public TimePeriodAdapter(Context context, ArrayList<TimePeriod> timePeriods) {
        super(context, 0, timePeriods);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimePeriod timePeriod = getItem(position);
        Date in = timePeriod.getInTimestamp();
        Date out = timePeriod.getOutTimestamp();

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_timeperiod, parent, false);
        }

        TextView inTimeView = (TextView) convertView.findViewById(R.id.in_time);
        TextView outTimeView = (TextView) convertView.findViewById(R.id.out_time);
        TextView durationTimeView = (TextView) convertView.findViewById(R.id.duration_time);
        TextView arrow = (TextView) convertView.findViewById(R.id.arrow);

        inTimeView.setText(formatTime(in));
        outTimeView.setText(formatOutTime(out));
        durationTimeView.setText(DateHelper.getDurationString(in, out));

        if(out == null) {
            arrow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // remove icon if text is empty
        } else {
            arrow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_forward_black_24dp, 0, 0, 0);
        }

        return convertView;
    }

    private String formatTime(Date date) {
        if(date == null) {
            return "";
        }

        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(context);
        return dateFormat.format(date);
    }

    private String formatOutTime(Date out) {
        if(out == null) {
            return "";
        }

        int flags =  DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_SHOW_TIME  | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NUMERIC_DATE;

        String formatted = DateUtils.formatDateTime(context, out.getTime(), flags);
        formatted = formatted.replace(",", "");
        return formatted;
    }
}
