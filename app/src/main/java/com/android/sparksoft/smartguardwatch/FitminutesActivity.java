package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.AlarmUtils;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Services.AlarmService;

import java.text.DecimalFormat;
import java.util.Calendar;

public class FitminutesActivity extends Activity {
    private static final String TAG = "AlarmNotifAct";
    AlarmManager alarmManager;
    private Button stopAlarm;
    private static FitminutesActivity inst;
    private TextView alarmMessage;
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

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitminutes);
        final SharedPreferences prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == Calendar.SUNDAY)
        {
            prefs.edit().putInt(Constants.FITMINUTES_COUNTER, 0).apply();
        }

        int fitminutes = prefs.getInt(Constants.FITMINUTES_COUNTER, 0);
        int fitminutesDuration = prefs.getInt(Constants.PREFS_FITMINUTE_DURATION, 150);
        double fitminutesPct = (double)fitminutes/fitminutesDuration;

        SeekBar sb = (SeekBar)findViewById(R.id.sbFitminsPct);
        sb.setMax(fitminutesDuration);
        sb.setProgress(fitminutes);

        DecimalFormat df = new DecimalFormat("00.00");
        TextView tvFitmins = (TextView)findViewById(R.id.tvFitminsStatus);
        tvFitmins.setText(fitminutes + "/" +
                        fitminutesDuration + "mins  " + df.format(fitminutesPct*100) + "% completed");
        Bundle data = getIntent().getExtras();
        alarm = data.getParcelable(Constants.ALARM);
        Button btnRemind = (Button)findViewById(R.id.btnFitminsRemind);
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmUtils.stopAlarm(getApplicationContext(), alarm);
                //Cancels the activityDetectionAlarm since user is awake (and cancelled the alarm)
                stopActivityDetectionAlarm();
                r.stop();
                finish();
            }
        });

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

        SpeechBot sp = new SpeechBot(this, alarm.getMemoryInstructions());
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onResume()
    {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitminutes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
