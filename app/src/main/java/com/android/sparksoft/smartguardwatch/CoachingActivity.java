package com.android.sparksoft.smartguardwatch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.AlarmUtils;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Services.AlarmService;

import org.json.JSONException;

import java.util.ArrayList;

public class CoachingActivity extends Activity {

    private static final String TAG = "AlarmNotifAct";
    AlarmManager alarmManager;
    private Button btnNo, btnYes;
    private static CoachingActivity inst;
    private TextView alarmMessage, alarmTitle;
    private TextView TvMemoryType;
    private String memoryId = "";
    private String memoryType = "";
    private Alarm alarm;
    private PowerManager.WakeLock mWakeLock;
    private Ringtone r;
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;
    private String memoryName = "";
    private SharedPreferences editor;
    private String appname;
    boolean isOver = false;

    public static CoachingActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaching);

        Log.d(TAG, "Start Alarm Activity");
        appname = getResources().getString(R.string.app_name);
        editor = getSharedPreferences(appname, Context.MODE_PRIVATE);

        btnNo = (Button) findViewById(R.id.btnCoachNo);
        btnYes = (Button) findViewById(R.id.btnCoachYes);
        alarmMessage = (TextView) findViewById(R.id.coachMessage);
        alarmTitle = (TextView)findViewById(R.id.coachTitle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        Bundle data = getIntent().getExtras();
        alarm = data.getParcelable(Constants.ALARM);
        if(alarm != null) {
            alarmTitle.setText(alarm.getMemoryName());
            alarmMessage.setText(alarm.getMemoryInstructions());
            memoryId = alarm.getMemoryId();
            memoryName = alarm.getMemoryName();
            memoryType = alarm.getMemoryType();

        }

        //For different behaviors of alarms
        //For WAKE alarm names
        if(memoryName.equals(Constants.ALARM_WAKE)) {
            //TODO: trigger another alarm 30 minutes from now to get the measurement of activity counter and erase it after analysis
            startActivityDetectionAlarm();
            try {
                ArrayList<Alarm> alarms = AlarmUtils.parseAlarmString(editor.getString("SampleAlarmString", ""));
                AlarmUtils.cancelAllAlarms(getApplicationContext(), alarms);
                Log.d(TAG, "cancelling all alarms");
            } catch (JSONException e) {
                Log.e(TAG, "JSONException= " + e);
            }
        } else {
            Log.d(TAG, "Other types of alarm: " + memoryName);
        }




        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {

            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.edit().putBoolean(Constants.USER_IS_AWAKE, true).apply();
                AlarmUtils.stopAlarm(getApplicationContext(), alarm);
                //Cancels the activityDetectionAlarm since user is awake (and cancelled the alarm)
                stopActivityDetectionAlarm();
                isOver = true;
                finish();
            }
        });


        final SpeechBot sp = new SpeechBot(this, alarm.getMemoryInstructions());
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);


        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOver = true;
                editor.edit().putBoolean(Constants.USER_IS_AWAKE, true).apply();
                AlarmUtils.stopAlarm(getApplicationContext(), alarm);
                //Cancels the activityDetectionAlarm since user is awake (and cancelled the alarm)
                stopActivityDetectionAlarm();
                sendSms();
                finish();

            }
        });


        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 60);
                    if(!isOver)
                    {
                        sp.talk(alarm.getMemoryInstructions(), false);
                        Thread.sleep(1000 * 60);
                        sendSms();
                        finish();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        new Thread(r).start();


    }

    private void sendSms()
    {
        ArrayList<Contact> arrayContacts;
        DataSourceContacts dsContacts;

        dsContacts = new DataSourceContacts(this);
        dsContacts.open();

        arrayContacts = dsContacts.getAllContacts();

        SmsManager sm;
        sm = SmsManager.getDefault();
        sm.sendTextMessage(arrayContacts.get(0).getMobile(), null, "I did not confirm my medication protocol: " + alarm.getMemoryName(), null, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {

        }
        return (keyCode == KeyEvent.KEYCODE_BACK ? true : super.onKeyDown(keyCode, event));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    public void startActivityDetectionAlarm() {
        Log.d(TAG, "startActivityDetectionAlarm");
        PendingIntent pendingIntent;
        AlarmManager manager;
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmService.class);
        alarmIntent.putExtra(Constants.ALARM_ACTIVITY_DETECT, Constants.ALARM_ACTIVITY_DETECT);
        pendingIntent = PendingIntent.getService(getApplicationContext(), Constants.ALARM_ACTIVITY_DETECT_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Constants.AFTER_WAKE_TIMER, pendingIntent);
    }

    public void stopActivityDetectionAlarm() {
        Log.d(TAG, "stopActivityDetectionAlarm");
        PendingIntent pendingIntent;
        AlarmManager manager;
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmService.class);
        alarmIntent.putExtra(Constants.ALARM_ACTIVITY_DETECT, Constants.ALARM_ACTIVITY_DETECT);
        pendingIntent = PendingIntent.getService(getApplicationContext(), Constants.ALARM_ACTIVITY_DETECT_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
