package de.jbdevelop.punchaclock.db;

public final class DatabaseContract {
    public DatabaseContract(){}

    public static abstract class AreaContract{
        public static final String TABLE_NAME = "Area";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_CREATEDON = "CreatedON";
        public static final String COLUMN_NAME_ENABLED = "Enabled";
        public static final String COLUMN_NAME_COORDINATES_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_COORDINATES_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_RADIUS = "Radius";
        public static final String COLUMN_NAME_CURRENTACTIVE = "CurrentActive";
        public static final String COLUMN_NAME_MONITORWIFI = "MonitorWifi";

    }

    public static abstract class TimePeriodContract{
        public static final String TABLE_NAME = "TimePeriod";
        public static final String COLUMN_NAME_TIMESTAMP_IN = "InTimestamp";
        public static final String COLUMN_NAME_TIMESTAMP_OUT = "OutTimestamp";
        public static final String COLUMN_NAME_FOREIGNKEY_AREA = "Area_Id";
        public static final String COLUMN_NAME_CURRENTACTIVE = "CurrentActive";
    }

    public static abstract class WifiContract{
        public static final String TABLE_NAME = "Wifi";
        public static final String COLUMN_NAME_SSID = "SSID";
        public static final String COLUMN_NAME_MAC = "MAC";
        public static final String COLUMN_NAME_FOREIGNKEY_AREA = "Area_Id";
    }
}
