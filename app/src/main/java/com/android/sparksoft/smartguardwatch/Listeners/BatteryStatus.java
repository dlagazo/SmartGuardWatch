package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;

import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Services.ChargingService;
import com.android.sparksoft.smartguardwatch.Services.FallService;

/**
 * Created by Daniel on 10/31/2015.
 */
public class BatteryStatus extends BroadcastReceiver{

    public BatteryStatus()
    {



    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent chargingService = new Intent(context, ChargingService.class);

        Intent fallIntent = new Intent(context, FallService.class);
        //startService(new Intent(getApplicationContext(), FallService.class));

        SharedPreferences prefs = context.getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);

        int logStatus = prefs.getInt(Constants.PREFS_LOGGED_IN, 0);

        if(action.equals(Intent.ACTION_POWER_CONNECTED) && logStatus == 1) {

            context.startService(chargingService);
            context.stopService(fallIntent);

            prefs.edit().putInt(Constants.PREFS_NAME, 1).apply();
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED) && logStatus == 1) {
            context.stopService(chargingService);
            context.startService(fallIntent);

            prefs.edit().putInt(Constants.PREFS_NAME, 1).apply();
        }
    }
}
