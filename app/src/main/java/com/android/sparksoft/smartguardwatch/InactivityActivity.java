package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Models.Constants;

public class InactivityActivity extends Activity {

    private boolean isOver = false;
    private PowerManager.WakeLock mWakeLock;
    private Ringtone r;
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;

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
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Inactivity");
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i("Inactivity", "Wakelock aquired!!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactivity);

        final SpeechBot sp = new SpeechBot(this, "");

        Button btnInactivityOk = (Button)findViewById(R.id.btnInactivityOk);
        btnInactivityOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(
                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(Constants.INACTIVE_COUNTER_SUCCESSIVE,0).apply();
                prefs.edit().putBoolean(Constants.INACTIVITY_ALARM, false).apply();
                isOver = true;
                r.stop();
                finish();
            }
        });

        Button btnInactivitySOS = (Button)findViewById(R.id.btnInactivitySOS);
        btnInactivitySOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Are you ok?", true);
                Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SharedPreferences prefs = getSharedPreferences(
                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(Constants.INACTIVE_COUNTER_SUCCESSIVE,0).apply();
                prefs.edit().putBoolean(Constants.INACTIVITY_ALARM, false).apply();

                Intent fallIntent = new Intent(getApplicationContext(), SOSActivity.class);
                fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(fallIntent);
                isOver = true;
                r.stop();
                finish();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 30);
                    if(!isOver)
                    {
                        r.stop();
                        sp.talk("Are you ok?", true);


                        Thread.sleep(2000);

                        SharedPreferences prefs = getSharedPreferences(
                                Constants.PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putInt(Constants.INACTIVE_COUNTER_SUCCESSIVE,0).apply();
                        prefs.edit().putBoolean(Constants.INACTIVITY_ALARM, false).apply();

                        Intent fallIntent = new Intent(getApplicationContext(), SOSActivity.class);
                        fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(fallIntent);
                        finish();

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inactivity, menu);
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
}
