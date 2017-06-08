package de.jbdevelop.punchaclock.model;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.jbdevelop.punchaclock.db.DatabaseContract;
import de.jbdevelop.punchaclock.helper.DateHelper;

@Table(name = DatabaseContract.TimePeriodContract.TABLE_NAME)
public class TimePeriod extends Model {


    @Column(name = DatabaseContract.TimePeriodContract.COLUMN_NAME_FOREIGNKEY_AREA, index = true)
    private Area area;

    @Column(name = DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_IN)
    private Date inTimestamp;

    @Column(name = DatabaseContract.TimePeriodContract.COLUMN_NAME_TIMESTAMP_OUT)
    private Date outTimestamp;

    @Column(name = DatabaseContract.TimePeriodContract.COLUMN_NAME_CURRENTACTIVE, index = true)
    private boolean currentActive = true;

    public TimePeriod() {
        super();
    }

    public TimePeriod(Area area) {
        super();
        this.area = area;
        this.inTimestamp = new Date();
    }

    public Area getArea() {
        return area;
    }

    public Date getInTimestamp() {
        return inTimestamp;
    }

    public void stop(){
        outTimestamp = new Date();
        currentActive = false;
    }

    public void setOutTimestamp(Date date) {
        outTimestamp = date;
    }

    public boolean isCurrentActive() {
        return currentActive;
    }

    public void setCurrentActive(boolean currentActive) {
        this.currentActive = currentActive;
    }

    public Date getOutTimestamp() {
        return outTimestamp;
    }

    @Override
    public String toString() {
        String text = "";

        java.text.DateFormat formatter = java.text.DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM);

        Date inTime = this.getInTimestamp();
        if(inTime != null) {
            text += formatter.format(inTime);
        }

        Date outTime = this.getOutTimestamp();

        if(outTime != null) {
            text += "  ->  ";

            if(!DateHelper.isSameDay(inTime, outTime)) {
                formatter = java.text.DateFormat.getDateTimeInstance();
            }
            text += formatter.format(outTime);
        }

        if(inTime != null && outTime != null) {
            text += "\n";
            text += DateUtils.formatElapsedTime((outTime.getTime() - inTime.getTime())/1000);
        }

        return text;
    }

    public void setInTimestamp(Date inTimestamp) {
        this.inTimestamp = inTimestamp;
    }
}
