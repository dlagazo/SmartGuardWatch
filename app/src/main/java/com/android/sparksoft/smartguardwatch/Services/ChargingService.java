package com.android.sparksoft.smartguardwatch.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;
import com.android.sparksoft.smartguardwatch.R;

import com.android.sparksoft.smartguardwatch.Features.FallDetector;
import com.android.sparksoft.smartguardwatch.Features.SoundMeter;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.SOSActivity;

import java.util.Timer;
import java.util.TimerTask;

public class ChargingService extends Service{
    int count = 0;
    PowerManager pm;
    PowerManager.WakeLock wl;
    int bufferSize;
    AudioRecord audio;
    double lastLevel;
    SoundMeter sm;
    int status;
    boolean isCharging;
    FallDetector fl;
    SpeechBot sp;

    Timer myTimer;

    boolean alarm = false;


    public ChargingService() {

        sm = new SoundMeter();
        sm.start();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        sp = new SpeechBot(getApplicationContext(), "");

        Toast.makeText(getApplicationContext(), "Charging protocol started. Please observe silence.", Toast.LENGTH_LONG).show();
        //sp.talk("Charging service started", false);

        //MediaPlayer mp = new MediaPlayer();
        //mp = MediaPlayer.create(ChargingService.this, R.raw.smartguard);
        //mp.start();
        syncData();



        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkNoise();

            }

        }, 0, 100);



        return 0;
    }

    private void syncData()
    {
        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        int status = prefs.getInt("sparksoft.smartguard.status", 0);

        if(status == 1)
        {
            String auth = prefs.getString("sparksoft.smartguard.auth", "");
            HelperLogin hr = new HelperLogin(getApplicationContext(), auth, sp);
            String url = "http://smartguardwatch.azurewebsites.net/api/MobileContact";
            hr.Sync(url);

            //Toast.makeText(getApplicationContext(), "Data syncing complete.", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkNoise()
    {
        Double noise = sm.getAmplitude();
        Log.e("ChargingService", Double.toString(sm.getAmplitude()));
        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        int SOSstatus = prefs.getInt("sparksoft.smartguard.SOSstatus", 1);
        if(noise > 20000 && !alarm && SOSstatus == 1)
        {
            sm.stop();
            sp.talk("Are you ok?", true);
            //Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();

            Intent fallIntent = new Intent(getApplicationContext(), SOSActivity.class);
            fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(fallIntent);
            alarm = true;

        }

        if(alarm && SOSstatus==0)
        {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alarm = false;
            sm.start();
            prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();

        }



    }

    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null)
            {

// Sense the voice…
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++)
                {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sm.stop();
        myTimer.purge();
        myTimer.cancel();

        String url = "http://smartguardwatch.azurewebsites.net/api/MobileCharge";
        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        String auth = prefs.getString("sparksoft.smartguard.auth", "");

        HelperLogin hr = new HelperLogin(getApplicationContext(), auth, sp);

        hr.sendChargeData(url);



        Toast.makeText(this, "Charging protocol stopped", Toast.LENGTH_LONG).show();
        //sp.talk("Charging service stopped", false);

        sp.destroy();

    }



}
