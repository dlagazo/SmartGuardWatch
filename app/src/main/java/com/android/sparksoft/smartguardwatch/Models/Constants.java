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
    public static final String HOME_LATITUDE = "HOME_LATITUDE";
    public static final String HOME_LONGITUDE = "HOME_LONGITUDE";

    //FALL DETECTOR SETTINGS
    public static final double FALL_THRESHOLD = 19.0; //FOR BOTH LINEAR and ACCELEROMETER
    public static final double MOVE_THRESHOLD = .4; //0.95 for linear, 0.60~ for accelerometer (more sensitive)
    public static final int UPPER_LIMIT_PEAK_COUNT = 5;
    public static final int LOWER_LIMIT_PEAK_COUNT = 0;
    public static final long FALL_DETECT_WINDOW_SECS = 5;
    public static final long VERIFY_FALL_DETECT_WINDOW_SECS = 9;
    public static final int SOS_PROTOCOL_ACTIVITY_ON = 1;
    public static final int SOS_PROTOCOL_ACTIVITY_OFF = 0;
    public static final String FALL_COUNTER = "FALL_COUNTER";

    //ACTIVITY PROTOCOL SETTINGS
    public static final double GRAVITY = 9.81;
    public static final double EIGHTYPERCENT = 0.80;
    public static final long CHARACTERIZE_ACTIVITY_WINDOW_SECS = 59; //59
    public static final double ACTIVE_THRESHOLD = 0.25;
    public static final double VERY_ACTIVE_THRESHOLD = 0.42;
    public static final int ACT_PROTOCOL_GATHERING_DATA = 0;
    public static final int ACT_PROTOCOL_ACTIVE_VERY_ACTIVE = 1;
    public static final int ACT_PROTOCOL_ACTIVE_ACTIVE = 2;
    public static final int ACT_PROTOCOL_INACTIVE_HORIZONTAL = 3;
    public static final int ACT_PROTOCOL_INACTIVE_VERTICAL = 4;
    public static final int AFTER_WAKE_TIMER = 60 * 30 * 1000;



    //NETWORK
    public static final String HOME_SSID = "\"Talusan Family Network\"";

    //SHARED PREFS
    public static final String PREFS_NEW_PRIORITY = "PRIORITY_LEVEL";
    public static final String PREFS_CURRENT_PRIORITY = "CURRENT_PRIORITY_LEVEL";
    public static final String PREFS_NAME = "sparksoft.smartguard";
    public static final String PREFS_SOS_PROTOCOL_ACTIVITY = "sparksoft.smartguard.SOSstatus";
    public static final String PREFS_AUTH = "sparksoft.smartguard.auth";
    public static final String PREFS_CHARGE_STATUS = "sparksoft.smartguard.status.charge";


    public static final String PREFS_CALL_STATUS = "sparksoft.smartguard.CallStatus";
    public static final String PREFS_LOGGED_IN = "sparksoft.smartguard.LogStatus";
    public static final String PREFS_LOG_CHECKER = "sparksoft.smartguard.loginStatus";
    public static final String PREFS_ALARM_STING = "sparksoft.smartguard.alarms";
    public static final String PREFS_SOS_CALL_STATUS = "sparksoft.smartguard.sosCallStatus";
    public static final String IS_USER_AT_HOME = "IS_USER_AT_HOME";
    public static final String EMERGENCY_STATUS = "EMERGENCY_STATUS";
    public static final String USER_CURRENT_LATITUDE = "USER_CURRENT_LATITUDE";
    public static final String USER_CURRENT_LONGITUDE = "USER_CURRENT_LONGITUDE";
    public static final String EMERGENCY_TIMER = "EMERGENCY_TIMER";
    //PREFS - FALL SERVICE PARAMETERS
    public static final String PREFS_FALL_PARAMETER_FUT = "sparksoft.smartguard.fallservice.fut";
    public static final String PREFS_FALL_PARAMETER_FLT = "sparksoft.smartguard.fallservice.flt";
    public static final String PREFS_FALL_PARAMETER_FWD = "sparksoft.smartguard.fallservice.fwd";
    public static final String PREFS_FALL_PARAMETER_PUT = "sparksoft.smartguard.fallservice.put";
    public static final String PREFS_FALL_PARAMETER_PLT = "sparksoft.smartguard.fallservice.plt";
    public static final String PREFS_FALL_PARAMETER_RMT = "sparksoft.smartguard.fallservice.rmt";
    public static final String PREFS_FALL_PARAMETER_RMD = "sparksoft.smartguard.fallservice.rmd";
    public static final String PREFS_FALL_PARAMETER_ACTIVE = "sparksoft.smartguard.fallservice.active";
    public static final String PREFS_FALL_PARAMETER_DESC = "sparksoft.smartguard.fallservice.desc";

    public static final String PREFS_GPS_LAT = "sparksoft.smartguard.gps.lat";
    public static final String PREFS_GPS_LONG = "sparksoft.smartguard.gps.long";
    //PREFS - ACTIVITY
    public static final String ACTIVE_COUNTER = "ACTIVE_COUNTER";
    public static final String INACTIVE_COUNTER = "INACTIVE_COUNTER";
    public static final String INACTIVE_COUNTER_SUCCESSIVE = "INACTIVE_COUNTER_SUCCESSIVE";
    public static final String FITMINUTES_COUNTER = "FITMINUTES_COUNTER";
    public static final String INACTIVITY_ALARM = "INACTIVITY_ALARM";
    public static final String PREFS_ACTIVITY_THRESHOLD = "sparksoft.smartguard.activity.threshold";
    public static final String PREFS_INACTIVITY_DURATION = "sparksoft.smartguard.activity.inactivityduration";
    public static final String PREFS_FITMINUTE_THRESHOLD = "sparksoft.smartguard.fitminute.threshold";
    public static final String PREFS_FITMINUTE_DURATION = "sparksoft.smartguard.fitminute.duration";

    //ALARMS
    public static final int ALARM_FREQUENCY_ONCE = 0;
    public static final int ALARM_FREQUENCY_DAILY = 1;
    public static final int ALARM_FREQUENCY_WEEKLY = 2;
    //    public static final int ALARM_FREQUENCY_MONTHLY = 3;
//    public static final int ALARM_FREQUENCY_YEARLY = 4;
//    public static final int ALARM_FREQUENCY_HOURLY = 5;
//    public static final int ALARM_FREQUENCY_CUSTOM = -1;
    public static final String ALARM_WAKE = "Wake";
    public static final String ALARM_ACTIVITY_DETECT = "Detect_Activity";
    public static final int ALARM_ACTIVITY_DETECT_ID = 1001;
    public static final String ALARM = "ALARM";

    //MEMORIES JSON
    public static final String MEMORIES = "memories";
    public static final String MEMORIES_MEMORYID = "MemoryId";
    public static final String MEMORIES_MEMORYNAME = "MemoryName";
    public static final String MEMORIES_FKUSERID = "fkUserId";
    public static final String MEMORIES_MEMORYFREQ = "MemoryFreq";
    public static final String MEMORIES_MEMORYINSTRUCTIONS = "MemoryInstructions";
    public static final String MEMORIES_MEMORYDATES = "MemoryDates";

    //TIME DATE CONSTANTS
    public static final int DAYS_IN_A_WEEK = 7;
    public static final int MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;
    public static final int MILLIS_IN_A_MINUTE = 60 * 1000;

    //API URLS
    public static final String URL_LOGIN = "http://smartguardwatch.azurewebsites.net/api/MobileContact";
    public static final String URL_CHARGEDATA = "http://smartguardwatch.azurewebsites.net/api/MobileCharge";
    public static final String URL_FALLDATA = "http://smartguardwatch.azurewebsites.net/api/MobileFall";

    //GEOFENCE
    public static final float FENCE_RADIUS_IN_METERS = 100; //in m
    public static final float LOCATION_ACCURACY = 40f; //meters
    public static final float NEGLIGIBLE_LOCATION_CHANGE = 10f;
    public static final long DEFAULT_UPDATE_INTERVAL_IN_SEC = 10000;
    public static final long DEFAULT_FASTEST_INTERVAL_IN_SEC = 15000;
    public static final int DEFAULT_DISPLACEMENT_IN_M = 2;



}