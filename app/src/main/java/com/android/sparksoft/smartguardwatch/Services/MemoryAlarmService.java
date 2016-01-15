package com.android.sparksoft.smartguardwatch.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.android.sparksoft.smartguardwatch.AlarmNotificationActivity;
import com.android.sparksoft.smartguardwatch.MemoryPlayActivity;

public class MemoryAlarmService extends IntentService {
    public MemoryAlarmService() {
        super("MemoryAlarmService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle data = intent.getExtras();
        Intent alarmIntent = new Intent(getBaseContext(), MemoryPlayActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtras(data);
        getApplication().startActivity(alarmIntent);
    }
}
