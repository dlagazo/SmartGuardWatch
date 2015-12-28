package com.android.sparksoft.smartguardwatch.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Models.AccelerometerData;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Utils;
import com.android.sparksoft.smartguardwatch.SOSActivity;

import java.util.ArrayList;

/**
 * Created by jtalusan on 10/13/2015.
 * http://stackoverflow.com/questions/5877780/orientation-from-android-accelerometer
 * https://github.com/AndroidExamples/android-sensor-example/blob/master/app/src/main/java/be/hcpl/android/sensors/service/SensorBackgroundService.java
 */

//import sqlitedb.SQLiteDataLogger;


/**
 * Created by jtalusan on 10/13/2015.
 * http://stackoverflow.com/questions/5877780/orientation-from-android-accelerometer
 * https://github.com/AndroidExamples/android-sensor-example/blob/master/app/src/main/java/be/hcpl/android/sensors/service/SensorBackgroundService.java
 * http://developer.android.com/training/articles/perf-tips.html
 */
public class FallService1 extends IntentService implements SensorEventListener
        //SQLiteDataLogger.AsyncResponse {
{
    private static final String DEBUG_TAG = "AccelService";
    private final ArrayList<UserFallListener> mListeners = new ArrayList<>();
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private ArrayList<AccelerometerData> accelerometerData;
    private ArrayList<AccelerometerData> activityProtocolData;
    private ArrayList<AccelerometerData> activityProtocolRawData;
    private ArrayList<AccelerometerData> potentiallyFallenData;
    private ArrayList<AccelerometerData> potentiallyFallenRawData;
    private float x, y, z = 0.0f;
    private int potentialFallCounter = 0;
    private boolean potentiallyFallen = false;
    private boolean actuallyFallen = false;
    //端末が実際に取得した加速度値。重力加速度も含まれる。This values include gravity force.
    private float[] currentOrientationValues = {0.0f, 0.0f, 0.0f};
    //ローパス、ハイパスフィルタ後の加速度値 Values after low pass and high pass filter
    private float[] currentAccelerationValues = {0.0f, 0.0f, 0.0f};
    //previous data 1つ前の値
    private float old_x = 0.0f;
    private float old_y = 0.0f;
    private float old_z = 0.0f;

    private boolean alarm = false;

    private String appname = "";
    private SharedPreferences editor;
    private SpeechBot sp;
    public FallService1() {
        super("FallService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = new SpeechBot(getApplicationContext(), "");
        Log.d(DEBUG_TAG, "onCreate");
        accelerometerData = new ArrayList<>();
        potentiallyFallenData = new ArrayList<>();
        activityProtocolData = new ArrayList<>();
        activityProtocolRawData = new ArrayList<>();
        potentiallyFallenRawData = new ArrayList<>();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //appname = getApplicationContext().getResources().getString(R.string.app_name);
        editor = getApplicationContext().getSharedPreferences("sparksoft.smartguard", Context.MODE_PRIVATE);

//        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
//            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        } else {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        }

        Toast.makeText(this, "Fall protocol started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
    }

    //TODO: Fix this, to just stop device gathering
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(DEBUG_TAG, "Start gathering v3");
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Fall protocol stopped", Toast.LENGTH_LONG).show();
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //https://gist.github.com/tomoima525/8395322 - Remove gravity factor
        if(editor.getInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY,
                Constants.SOS_PROTOCOL_ACTIVITY_OFF) == 0 && alarm)
        {
            alarm = false;
            editor.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();
            Toast.makeText(this, "Fall protocol restarted", Toast.LENGTH_SHORT).show();
        }
        else if (editor.getInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY,
                Constants.SOS_PROTOCOL_ACTIVITY_OFF) == 1 && !alarm) { //TODO: Should turn SOS ON in other method
            float[] rawAcceleration = event.values.clone();

            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
            } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // ローパスフィルタで重力値を抽出　Isolate the force of gravity with the low-pass filter.
                currentOrientationValues[0] = event.values[0] * 0.1f + currentOrientationValues[0] * (1.0f - 0.1f);
                currentOrientationValues[1] = event.values[1] * 0.1f + currentOrientationValues[1] * (1.0f - 0.1f);
                currentOrientationValues[2] = event.values[2] * 0.1f + currentOrientationValues[2] * (1.0f - 0.1f);

                // 重力の値を省くRemove the gravity contribution with the high-pass filter.
                currentAccelerationValues[0] = event.values[0] - currentOrientationValues[0];
                currentAccelerationValues[1] = event.values[1] - currentOrientationValues[1];
                currentAccelerationValues[2] = event.values[2] - currentOrientationValues[2];

                // ベクトル値を求めるために差分を計算　diff for vector
                x = currentAccelerationValues[0] - old_x;
                y = currentAccelerationValues[1] - old_y;
                z = currentAccelerationValues[2] - old_z;

                // 状態更新
                old_x = currentAccelerationValues[0];
                old_y = currentAccelerationValues[1];
                old_z = currentAccelerationValues[2];
            }

            //ACTIVITY SENSOR
            if (!Utils.isAccelerometerArrayExceedingTimeLimit(activityProtocolRawData, Constants.CHARACTERIZE_ACTIVITY_WINDOW_SECS)) {
                activityProtocolRawData.add(new AccelerometerData(Utils.getCurrentTimeStampInSeconds(), rawAcceleration[0], rawAcceleration[1], rawAcceleration[2]));
                activityProtocolData.add(new AccelerometerData(Utils.getCurrentTimeStampInSeconds(), x, y, z));
            } else { //End of activity protocol sensor window (CHARACTERIZE_ACTIVITY_WINDOW_SECS)
                Log.d(DEBUG_TAG, "End of characterizing activity window");
                double[] rawActivityProtocolAccelerationPerAxis = Utils.getAverageAccelerationPerAxis(activityProtocolRawData);
                Log.d(DEBUG_TAG, "Ave for activity: " + Utils.getAverageNormalizedAcceleration(activityProtocolData));
                if ((Utils.getAverageNormalizedAcceleration(activityProtocolData) > Constants.ACTIVE_THRESHOLD) &&
                        Utils.getAverageNormalizedAcceleration(activityProtocolData) < Constants.VERY_ACTIVE_THRESHOLD) { //ACTIVE
                    //TODO: Flag as very active
                    Log.d(DEBUG_TAG, "Active: Active");
                } else if(Utils.getAverageNormalizedAcceleration(activityProtocolData) > Constants.VERY_ACTIVE_THRESHOLD) { //VERY ACTIVE
                    //TODO: Flag as active
                    Log.d(DEBUG_TAG, "Active: Very Active");
                } else { //INACTIVE
                    if(checkIfDeviceIsHorizontalToGround(rawActivityProtocolAccelerationPerAxis)) {
                        //TODO: Flag as inactive - Horizontal
                        Log.d(DEBUG_TAG, "Inactive: Horizontal");
                    } else {
                        //TODO: Flag as inactive - Vertical
                        Log.d(DEBUG_TAG, "Inactive: Vertical");
                    }
                }
                //TODO: Log to SQLiteDB
                activityProtocolRawData.clear();
                activityProtocolData.clear();
            }
            //END ACTIVITY SENSOR

            //START FALL DETECTOR
            if (!Utils.isAccelerometerArrayExceedingTimeLimit(accelerometerData, Constants.FALL_DETECT_WINDOW_SECS) && !potentiallyFallen) {
                AccelerometerData a = new AccelerometerData(Utils.getCurrentTimeStampInSeconds(), x, y, z);
                accelerometerData.add(a);
//            Log.d(DEBUG_TAG, a.toString() + "/" + a.getNormalizedAcceleration());
            } else if (potentiallyFallen) {
                Log.d(DEBUG_TAG, "Start Potential Fall Cycle : " + Utils.getAverageNormalizedAcceleration(accelerometerData));
                if (!Utils.isAccelerometerArrayExceedingTimeLimit(accelerometerData, Constants.VERIFY_FALL_DETECT_WINDOW_SECS)) {
                    AccelerometerData a = new AccelerometerData(Utils.getCurrentTimeStampInSeconds(), x, y, z);
                    accelerometerData.add(a);
                    AccelerometerData raw = new AccelerometerData(Utils.getCurrentTimeStampInSeconds(), rawAcceleration[0], rawAcceleration[1], rawAcceleration[2]);
                    potentiallyFallenRawData.add(raw);

                } else { //Not moving past MOVE_THRESHOLD after 10 seconds
                    Log.d(DEBUG_TAG, "End of 10 second potential fall cycle");
                    double[] rawAccelerometerData = Utils.getAverageAccelerationPerAxis(potentiallyFallenRawData);
                    Log.d(DEBUG_TAG, "Raw:" + rawAccelerometerData[0] + "," + rawAccelerometerData[1] + "," + rawAccelerometerData[2] );
                    if (Utils.getAverageNormalizedAcceleration(accelerometerData) > Constants.MOVE_THRESHOLD) {
                        Log.d(DEBUG_TAG, "Ave: " + Utils.getAverageNormalizedAcceleration(accelerometerData));
                        potentiallyFallen = false;
                        Log.d(DEBUG_TAG, "False alarm 1");
                        accelerometerData.clear();
                        Toast.makeText(this, "False alarm. Signs of significant movement detected.", Toast.LENGTH_SHORT).show();
                    } else if (checkIfDeviceIsHorizontalToGround(rawAccelerometerData)) { //TODO: Smartguard is horizontal to the ground
                        Toast.makeText(getApplicationContext(), "Arm is horizontal", Toast.LENGTH_SHORT).show();
                        actuallyFallen = true;
                        //TODO: Prompt user if they are ok.
                        alarm = true;
                        sp.talk("Are you ok?", true);

                        Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        accelerometerData.clear();
                        Intent fallIntent = new Intent(getApplicationContext(), SOSActivity.class);
                        fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(fallIntent);

                        Log.d(DEBUG_TAG, "Actual Fall! Ave movement:" + Utils.getAverageNormalizedAcceleration(accelerometerData));

                        //sensorManager.unregisterListener(this);
                        //TODO: Log potentiallyFallenData
                        //SQLiteDataLogger logger = new SQLiteDataLogger(this);
                        //logger.execute(accelerometerData);
                        //logger.delegate = this;
                    } else { //TODO: just catch inadvertent conditions (RESET)
                        potentiallyFallen = false;
                        Log.d(DEBUG_TAG, "False alarm 2");
                        accelerometerData.clear();
                    }
                    potentiallyFallenData.clear();
                }
            } else if (actuallyFallen) {


                //sensorManager.unregisterListener(this);

                Log.d(DEBUG_TAG, "Call contacts");
            } else {
                Log.d(DEBUG_TAG, "End of 5 second detection cycle.");
                potentialFallCounter = Utils.getNumberOfPeaksThatExceedThreshold(accelerometerData, Constants.FALL_THRESHOLD);
                Log.d(DEBUG_TAG, "potential fall count: " + potentialFallCounter);
                if (potentialFallCounter > Constants.LOWER_LIMIT_PEAK_COUNT && potentialFallCounter < Constants.UPPER_LIMIT_PEAK_COUNT) {
                    potentiallyFallenData = accelerometerData;
                    potentiallyFallen = true;
                    Toast.makeText(getApplicationContext(), "Potential fall detected. Check for arm " +
                            "orientation for the next 10 seconds", Toast.LENGTH_LONG).show();
                    Log.d(DEBUG_TAG, "Tagged as potential fall, switching to 10 second cycle");
                } else {
                    Log.d(DEBUG_TAG, "No fall detected");
                }
                potentialFallCounter = 0;
                accelerometerData.clear();
            }
            //END FALL DETECTOR
        } else { //Reset flags
            accelerometerData.clear();
            activityProtocolRawData.clear();
            potentiallyFallenRawData.clear();
            potentiallyFallenData.clear();
            potentiallyFallen = false;
            actuallyFallen = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
    @Override
    public void processIsFinished(boolean output) {
        if (output) {
//            editor.edit().putInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY, Constants.SOS_PROTOCOL_ACTIVITY_ON).apply();
            Log.d(DEBUG_TAG, "Successfully saved to DB.");
        } else {
            Log.d(DEBUG_TAG, "Failed to save to DB, please try again.");
        }
    }
    */

    /**
     * Calls registered event listeners
     */
    private void notifyListeners(int activity) {
        if (activity == 0) return;
        for (UserFallListener listener : mListeners) {
            listener.onUserFall(activity);
            Log.d(DEBUG_TAG, String.valueOf(activity));
        }
    }

    public interface UserFallListener {
        /**
         * Called when leg state have changed
         */
        void onUserFall(int activity);
    }

    private boolean checkIfDeviceIsHorizontalToGround(double[] averageAcceleration) {
        double hypotenuse = Math.sqrt(Math.pow(averageAcceleration[1], 2) + Math.pow(averageAcceleration[2], 2));
        if(hypotenuse > (Constants.GRAVITY * Constants.EIGHTYPERCENT)) {
            Log.d(DEBUG_TAG, "Hypotenuse: " + hypotenuse);
            return true;
        } else {
            return false;
        }
    }
}