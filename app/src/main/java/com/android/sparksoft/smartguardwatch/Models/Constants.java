package com.android.sparksoft.smartguardwatch.Models;

/**
 * Created by Daniel on 11/4/2015.
 */
public class Constants {
    //COLUMN_NAMES
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TIMESSTAMP = "TIMESTAMP";
    public static final String COLUMN_XAXIS = "XAXIS";
    public static final String COLUMN_YAXIS = "YAXIS";
    public static final String COLUMN_ZAXIS = "ZAXIS";

    //KEYS
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String XAXIS = "XAXIS";
    public static final String YAXIS = "YAXIS";
    public static final String ZAXIS = "ZAXIS";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    //FALL DETECTOR SETTINGS
    public static final double FALL_THRESHOLD = 10.0; //FOR BOTH LINEAR and ACCELEROMETER
    public static final double MOVE_THRESHOLD = 10.0; //0.95 for linear, 0.60~ for accelerometer (more sensitive)
    public static final int UPPER_LIMIT_PEAK_COUNT = 5;
    public static final int LOWER_LIMIT_PEAK_COUNT = 0;
    public static final long FALL_DETECT_WINDOW_SECS = 5;
    public static final long VERIFY_FALL_DETECT_WINDOW_SECS = 9;
    public static final int SOS_PROTOCOL_ACTIVITY_ON = 1;
    public static final int SOS_PROTOCOL_ACTIVITY_OFF = 0;

    //ACTIVITY PROTOCOL SETTINGS
    public static final double GRAVITY = 9.81;
    public static final double EIGHTYPERCENT = 0.80;
    public static final long CHARACTERIZE_ACTIVITY_WINDOW_SECS = 59; //59
    public static final double ACTIVE_THRESHOLD = 1.0;
    public static final double VERY_ACTIVE_THRESHOLD = 3.0;

    //GEOFENCE
    public static final float FENCE_RADIUS_IN_METERS = 100f;
    public static final int LOCATION_ACCURACY = 20;
    public static final float NEGLIGIBLE_LOCATION_CHANGE = 10f;
    public static final long DEFAULT_UPDATE_INTERVAL_IN_SEC = 10000;
    public static final long DEFAULT_FASTEST_INTERVAL_IN_SEC = 15000;
    public static final int DEFAULT_DISPLACEMENT_IN_M = 2;

    //NETWORK
    public static final String HOME_SSID = "\"Talusan Family Network\"";

    //SHARED PREFS
    public static final String PREFS_NEW_PRIORITY = "PRIORITY_LEVEL";
    public static final String PREFS_CURRENT_PRIORITY = "CURRENT_PRIORITY_LEVEL";
    public static final String PREFS_SOS_PROTOCOL_ACTIVITY = "sparksoft.smartguard.SOSstatus";


    //ALARMS
    public static final int ALARM_FREQUENCY_ONCE = 0;
    public static final int ALARM_FREQUENCY_DAILY = 1;
    public static final int ALARM_FREQUENCY_WEEKLY = 2;
    public static final int ALARM_FREQUENCY_MONTHLY = 3;
    public static final int ALARM_FREQUENCY_YEARLY = 4;
    public static final int ALARM_FREQUENCY_HOURLY = 5;
    public static final int ALARM_FREQUENCY_CUSTOM = -1;
    public static final String ALARM_WAKE = "WAKE";
    public static final String ALARM = "ALARM";

    //MEMORIES JSON
    public static final String MEMORIES = "memories";
    public static final String MEMORIES_MEMORYID = "MemoryId";
    public static final String MEMORIES_MEMORYNAME = "MemoryName";
    public static final String MEMORIES_FKUSERID = "fkUserId";
    public static final String MEMORIES_MEMORYFREQ = "MemoryFreq";
    public static final String MEMORIES_MEMORYINSTRUCTIONS = "MemoryInstructions";
    public static final String MEMORIES_MEMORYDATES = "MemoryDates";
}