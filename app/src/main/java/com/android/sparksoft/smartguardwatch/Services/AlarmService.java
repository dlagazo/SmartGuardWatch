package com.android.sparksoft.smartguardwatch.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Button;

import com.android.sparksoft.smartguardwatch.AlarmNotificationActivity;
import com.android.sparksoft.smartguardwatch.CoachingActivity;
import com.android.sparksoft.smartguardwatch.FitminutesActivity;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.WeightActivity;

import org.xml.sax.Parser;

public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";
    private String appname;
    private SharedPreferences editor;
    public AlarmService() {
        super("ScheduledService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        editor = getSharedPreferences(appname, Context.MODE_PRIVATE);
        Log.d(TAG, "onCreate:AlarmService");
    }

    @Override
    protected  void onHandleIntent(Intent intent) {
        Bundle data = intent.getExtras();
        //Behavior when alarm, set by Alarm.java is triggered (reminder to user)
        if(data.getParcelable(Constants.ALARM) != null) {
            Alarm alarm = data.getParcelable(Constants.ALARM);
            Log.d(TAG, alarm.toString());
//            Log.d(TAG, "AlarmId: " + alarm.getMemoryId());
//            if (alarm.getMemoryDates() != null) {
//                for (int i = 0; i < alarm.getMemoryDates().length; ++i) {
//                    Log.d(TAG, "MemoryDates: " + alarm.getMemoryDates()[i]);
//                }
//            }
//            Log.d(TAG, alarm.getMemoryInstructions());
            //TODO: Add checking if alarm notification activity is currently open.
            if(alarm.getMemoryType().equals("0"))
            {
                Intent alarmIntent = new Intent(getBaseContext(), AlarmNotificationActivity.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtras(intent);
                getApplication().startActivity(alarmIntent);
            }
            else if(alarm.getMemoryType().equals("1"))
            {

                Intent alarmIntent = new Intent(getBaseContext(), CoachingActivity.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtras(intent);
                getApplication().startActivity(alarmIntent);

            }
            else if(alarm.getMemoryType().equals("2"))
            {
                Intent alarmIntent = new Intent(getBaseContext(), FitminutesActivity.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtras(intent);
                getApplication().startActivity(alarmIntent);
            }
            else if(alarm.getMemoryType().equals("3"))
            {
                Intent alarmIntent = new Intent(getBaseContext(), AlarmNotificationActivity.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtras(intent);
                getApplication().startActivity(alarmIntent);
            }
            //Measure weight
            else if(alarm.getMemoryType().equals("4"))
            {
                Intent alarmIntent = new Intent(getBaseContext(), WeightActivity.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtras(intent);
                getApplication().startActivity(alarmIntent);
            }


        }

        //Different ALARM behavior that is set when the alarm WAKE is triggered, triggers after 30 seconds
        if (data.getString(Constants.ALARM_ACTIVITY_DETECT) != null &&
                data.getString(Constants.ALARM_ACTIVITY_DETECT).equals(Constants.ALARM_ACTIVITY_DETECT)) {
            Log.d(TAG, "Alarm Activity Detect");
            Log.d(TAG, "Activity Count: " + editor.getInt(Constants.ACTIVE_COUNTER, 0));
            if(editor.getInt(Constants.ACTIVE_COUNTER, 0) == 0) {
                Log.d(TAG, "User inactive for " + Constants.AFTER_WAKE_TIMER / Constants.MILLIS_IN_A_MINUTE + " minutes.");
            } else if(!editor.getBoolean(Constants.USER_IS_AWAKE, false)) { //Emergency protocol if alarm has not been pressed in 30 minutes
                Log.d(TAG, "User has not pressed alarm for " + Constants.AFTER_WAKE_TIMER / Constants.MILLIS_IN_A_MINUTE + " minutes.");
            } else {
                editor.edit().putInt(Constants.ACTIVE_COUNTER, 0).apply();
                editor.edit().putInt(Constants.INACTIVE_COUNTER, 0).apply();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }
}