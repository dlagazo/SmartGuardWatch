package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.android.sparksoft.smartguardwatch.Services.ChargingService;

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





        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            context.startService(chargingService);
        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            context.stopService(chargingService);
        }
    }
}
