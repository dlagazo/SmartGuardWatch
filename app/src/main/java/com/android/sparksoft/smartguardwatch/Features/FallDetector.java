package com.android.sparksoft.smartguardwatch.Features;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.Vibrator;

import com.android.sparksoft.smartguardwatch.MainActivity;

/**
 * Created by Daniel on 9/26/2015.
 */
public class FallDetector implements SensorEventListener{
    private Sensor accelerometer;
    float x, y, z;
    private SensorManager sensorManager;
    private Context _context;
    private boolean fallDetected;
    private SpeechBot speechBot;
    private PowerManager.WakeLock wl;


    public FallDetector(Context context)
    {
        fallDetected = true;
        _context = context;
        speechBot = new SpeechBot(context, null);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            //vibrateThreshold = accelerometer.getMaximumRange() / 2;

        }
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        if((Math.abs(x) + Math.abs(y) + Math.abs(z)) > 40 && fallDetected) {
            Vibrator v = (Vibrator) _context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);


            //tts.speak("Are you ok?", TextToSpeech.QUEUE_FLUSH, null);
            speechBot.talk("Are you ok? Please speak after the tone.", false);

            PowerManager pm = (PowerManager) _context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "bbbb");
            wl.acquire();

            /*
            Intent dialogIntent = new Intent(_context.getApplicationContext(), MainActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(dialogIntent);
            */



            fallDetected = false;
        }
    }

    public void resetDetector()
    {
        fallDetected = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
