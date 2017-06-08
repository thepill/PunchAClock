package de.jbdevelop.punchaclock.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.jbdevelop.punchaclock.R;
import de.jbdevelop.punchaclock.db.TimePeriodRepository;
import de.jbdevelop.punchaclock.model.TimePeriod;
import de.jbdevelop.punchaclock.ui.TimeperiodListActivity;
import de.jbdevelop.punchaclock.ui.AreaSettingsActivity;
import de.jbdevelop.punchaclock.model.Area;

/**
 * Created by jan on 26.10.15.
 */
public class AreaListAdapter extends ArrayAdapter<Area> {

    public AreaListAdapter(Context context, ArrayList<Area> areas) {
        super(context, 0, areas);
    }

    public static final String AREA_ID_KEY = "areaId";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Area area = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main_area, parent, false);
        }
        TextView areaName = (TextView) convertView.findViewById(R.id.areaName);
        areaName.setText(area.getDescription());

        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.areaListItem);

        if(area.isCurrentActive()) {
            container.setBackgroundColor(Color.LTGRAY);
        } else {
            container.setBackgroundColor(Color.WHITE);
        }

        View.OnClickListener startAreaDetail = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showAreaDetail = new Intent(getContext(), TimeperiodListActivity.class);
                showAreaDetail.putExtra(AREA_ID_KEY, area.getId());
                getContext().startActivity(showAreaDetail);
            }
        };

        container.setOnClickListener(startAreaDetail);

        initInOutText(convertView, area);

        return convertView;
    }

    private void initInOutText(View convertView, Area area) {
        TextView inText = (TextView) convertView.findViewById(R.id.inText);
        TextView outText = (TextView) convertView.findViewById(R.id.outText);

        try {
            TimePeriod timePeriod = TimePeriodRepository.getInstance().getLastTimeperiodForArea(area);
            if(timePeriod != null) {
                Date in = timePeriod.getInTimestamp();
                if(in != null) {
                    inText.setVisibility(View.VISIBLE);
                    inText.setText(formatDate(timePeriod.getInTimestamp()));
                } else {
                    inText.setVisibility(View.INVISIBLE);
                }

                Date out = timePeriod.getOutTimestamp();
                if(out != null) {
                    outText.setVisibility(View.VISIBLE);
                    outText.setText(formatDate(timePeriod.getOutTimestamp()));
                } else {
                    outText.setVisibility(View.INVISIBLE);
                }
            }
        } catch (NoSuchFieldException e) {
        }
    }

    private String formatDate(Date date) {
        DateFormat dateFormat1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return dateFormat1.format(date);
    }
}
