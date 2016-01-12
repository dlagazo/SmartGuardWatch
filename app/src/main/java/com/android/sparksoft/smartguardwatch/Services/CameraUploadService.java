package com.android.sparksoft.smartguardwatch.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioRecord;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.FallDetector;
import com.android.sparksoft.smartguardwatch.Features.SoundMeter;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;
import com.android.sparksoft.smartguardwatch.SOSActivity;

import java.util.Timer;
import java.util.TimerTask;

public class CameraUploadService extends Service{


    Timer myTimer;

    boolean alarm = false;


    public CameraUploadService() {




    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {





        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                upload();

            }

        }, 0, 5000);



        return 0;
    }



    private void upload()
    {




    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        myTimer.purge();
        myTimer.cancel();

    }



}
