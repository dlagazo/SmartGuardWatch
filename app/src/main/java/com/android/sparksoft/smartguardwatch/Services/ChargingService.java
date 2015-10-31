package com.android.sparksoft.smartguardwatch.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
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

        //MediaPlayer mp = new MediaPlayer();
        //mp = MediaPlayer.create(ChargingService.this, R.raw.smartguard);
        //mp.start();

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkNoise();

            }

        }, 0, 100);



        return 0;
    }

    private void checkNoise()
    {
        Double noise = sm.getAmplitude();
        Log.e("ChargingService", Double.toString(sm.getAmplitude()));
        if(noise > 10000 && !alarm)
        {
            sp.talk("Do you need an emergency call?", true);
            sm.stop();
            Intent fallIntent = new Intent(getApplicationContext(), SOSActivity.class);
            fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(fallIntent);
            alarm = true;
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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



}
