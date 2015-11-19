package com.android.sparksoft.smartguardwatch.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.sparksoft.smartguardwatch.AlarmNotificationActivity;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.Constants;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AlarmService extends IntentService {
    private static final String TAG = "Wearable.AlarmService";
    public AlarmService() {
        super("ScheduledService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:AlarmService");
    }

    @Override
    protected  void onHandleIntent(Intent intent) {
        Bundle data = intent.getExtras();
        Alarm alarm = data.getParcelable(Constants.ALARM);

        Log.d(TAG, "AlarmId: " + alarm.getMemoryId());

        //TODO: Add checking if alarm notification activity is currently open.
        Intent alarmIntent = new Intent(getBaseContext(), AlarmNotificationActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtras(intent);
        getApplication().startActivity(alarmIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }
}
