package com.android.sparksoft.smartguardwatch.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.FallDetector;
import com.android.sparksoft.smartguardwatch.Features.SoundMeter;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.R;

public class SmartGuardService extends Service{
    int count = 0;



    SpeechBot sp;


    public SmartGuardService() {
        sp = new SpeechBot(this, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    PowerManager pm;
    PowerManager.WakeLock wl;
    int bufferSize;
    AudioRecord audio;
    double lastLevel;
    SoundMeter sm;
    int status;
    boolean isCharging;
    FallDetector fl;


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {


        /*
        MediaPlayer mp = new MediaPlayer();


        try {
            mp.setDataSource("/storage/emulated/0/Samsung/Music/Over the Horizon.mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);

        for(int i=0; i < 10; i++)
        {
            sp.talk("This is an emergency call from smart guard.", true);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //MediaPlayer mp = new MediaPlayer();
        //mp = MediaPlayer.create(SmartGuardService.this, R.raw.smartguard);
        //mp.start();

        return 0;

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

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



}
