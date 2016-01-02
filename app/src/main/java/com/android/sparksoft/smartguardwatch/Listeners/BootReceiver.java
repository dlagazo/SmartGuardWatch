package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.sparksoft.smartguardwatch.MainActivity;
import com.android.sparksoft.smartguardwatch.Services.FallService;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);

        /*
        SharedPreferences prefs = context.getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        int status = prefs.getInt("sparksoft.smartguard.status", 0);
        if(status == 1)
        {
            Intent fallIntent = new Intent(context, FallService.class);
            //startService(new Intent(getApplicationContext(), FallService.class));
            context.stopService(fallIntent);
            context.startService(fallIntent);
        }
        */

    }
}
