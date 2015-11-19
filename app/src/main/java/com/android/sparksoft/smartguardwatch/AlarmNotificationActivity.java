package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.Constants;

import java.util.Calendar;



/**
 * Created by talusan on 11/11/2015.
 */
public class AlarmNotificationActivity extends Activity {
    private static final String TAG = "AlarmNotifAct";
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Button stopAlarm;
    private static AlarmNotificationActivity inst;
    private TextView alarmMessage, alarmTitle, alarmSchedule;
    private String memoryId = "";
    private Alarm alarm;
    private PowerManager.WakeLock mWakeLock;
    private Ringtone r;
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;

    public static AlarmNotificationActivity instance() {
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
        setContentView(R.layout.activity_alarm_notification);
        stopAlarm = (Button) findViewById(R.id.stopAlarm);
        alarmTitle = (TextView) findViewById(R.id.alarmTitle);
        alarmMessage = (TextView) findViewById(R.id.alarmMessage);
        alarmSchedule = (TextView) findViewById(R.id.alarmSchedule);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Bundle data = getIntent().getExtras();
        alarm = data.getParcelable(Constants.ALARM);

        alarmTitle.setText(alarm.getMemoryName());
        alarmMessage.setText(alarm.getMemoryInstructions());
        alarmSchedule.setText(alarm.getMemorySched());
        memoryId = alarm.getMemoryId();

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        r = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        r.play();

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

        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.stopAlarm(getApplicationContext());
                r.stop();
                finish();
            }
        });
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
}