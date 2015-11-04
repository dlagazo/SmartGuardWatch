package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;

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





        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {

            context.startService(chargingService);
            context.stopService(fallIntent);
            SharedPreferences prefs = context.getSharedPreferences(
                    "sparksoft.smartguard", Context.MODE_PRIVATE);
            prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            context.stopService(chargingService);
            context.startService(fallIntent);
            SharedPreferences prefs = context.getSharedPreferences(
                    "sparksoft.smartguard", Context.MODE_PRIVATE);
            prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();
        }
    }
}
