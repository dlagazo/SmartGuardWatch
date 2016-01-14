package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;

import com.android.sparksoft.smartguardwatch.ChargeActivity;
import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Services.ChargingService;
import com.android.sparksoft.smartguardwatch.Services.FallService;
import com.android.sparksoft.smartguardwatch.Services.LocationSensorService;

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
        Intent locationIntent = new Intent(context, LocationSensorService.class);
        Intent fallIntent = new Intent(context, FallService.class);
        //startService(new Intent(getApplicationContext(), FallService.class));

        SharedPreferences prefs = context.getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);

        int logStatus = prefs.getInt(Constants.PREFS_LOGGED_IN, 0);

        if(action.equals(Intent.ACTION_POWER_CONNECTED) && logStatus == 1) {
            prefs.edit().putInt("chargeStatus", 1).apply();
            prefs.edit().putInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY, 1).apply();
            context.startService(chargingService);
            context.stopService(fallIntent);
            Intent chargeActivity = new Intent(context, ChargeActivity.class);
            chargeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chargeActivity);
            prefs.edit().putInt("sparksoft.smartguard.chargeStatus", 1).apply();
            prefs.edit().putBoolean(Constants.PREFS_CHARGE_STATUS, true).apply();


            context.stopService(locationIntent);

        }
        else if(action.equals(Intent.ACTION_POWER_DISCONNECTED) && logStatus == 1) {
            prefs.edit().putInt("chargeStatus", 0).apply();
            context.stopService(chargingService);
            context.startService(fallIntent);
            context.startService(locationIntent);
            prefs.edit().putInt("sparksoft.smartguard.chargeStatus", 0).apply();
            prefs.edit().putBoolean(Constants.PREFS_CHARGE_STATUS, false).apply();
            String auth = prefs.getString("sparksoft.smartguard.auth", "");


        }
    }
}
