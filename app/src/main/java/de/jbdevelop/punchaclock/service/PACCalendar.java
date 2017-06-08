package de.jbdevelop.punchaclock.service;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;

import java.util.TimeZone;

import de.jbdevelop.punchaclock.db.AreaRepository;
import de.jbdevelop.punchaclock.helper.PACLog;
import de.jbdevelop.punchaclock.model.TimePeriod;

/**
 * Created by Benjamin on 26.04.16.
 */
public class PACCalendar {
    private final String LOGCAT_TAG = "PAC-" + PACCalendar.class.getSimpleName();
    private final String NAME = "PunchAClock";
    private final String DISPLAY_NAME = "PunchAClock";

    private Context context;
    private ContentResolver contentResolver;

    public PACCalendar(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public void build() {
        if (exists()) {
            return;
        }

        Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, NAME);
        cv.put(CalendarContract.Calendars.NAME, NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, DISPLAY_NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
        cv.put(CalendarContract.Calendars.VISIBLE, 1);
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        calUri = calUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
        contentResolver.insert(calUri, cv);
    }

    public boolean exists() {
        Cursor cur = null;
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME};

        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ?";
        String[] selectionArgs = new String[]{NAME};

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            PACLog.i(LOGCAT_TAG, "Permission denied");
            return true;
        }
        cur = contentResolver.query(uri, projection, selection, selectionArgs, null);

        boolean exists = cur.moveToFirst();
        return exists;
    }

    public int getId() {
        if (!exists()) {
            throw new Resources.NotFoundException("Calender named " + NAME + " not found!");
        }

        Cursor cur = null;
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[]{CalendarContract.Calendars._ID};

        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ?";
        String[] selectionArgs = new String[]{NAME};

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            PACLog.i(LOGCAT_TAG, "Permission denied");
            return Integer.MIN_VALUE;
        }
        cur = contentResolver.query(uri, projection, selection, selectionArgs, null);

        cur.moveToFirst();
        int colId = cur.getColumnIndex(CalendarContract.Calendars._ID);
        return cur.getInt(colId);
    }

    public void addTimePeriod(TimePeriod timePeriod) {
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, timePeriod.getInTimestamp().getTime());
        values.put(Events.DTEND, timePeriod.getOutTimestamp().getTime());
        values.put(Events.TITLE, timePeriod.getArea().getDescription());
        values.put(Events.CALENDAR_ID, getId());
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            PACLog.i(LOGCAT_TAG, "Permission denied");
            return;
        }
        contentResolver.insert(Events.CONTENT_URI, values);
    }

}
