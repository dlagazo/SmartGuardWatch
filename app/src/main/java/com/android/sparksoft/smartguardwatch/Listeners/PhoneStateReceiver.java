package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.SOSMessageActivity;
import com.android.sparksoft.smartguardwatch.Services.SmartGuardService;
import com.android.sparksoft.smartguardwatch.NavigateActivity;
import com.android.sparksoft.smartguardwatch.Services.SmartGuardService;

import java.lang.reflect.Method;

/**
 * Created by Daniel on 10/1/2015.
 */
public class PhoneStateReceiver extends BroadcastReceiver {


    public static String TAG="PhoneStateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
        {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            //Log.d(TAG, "PhoneStateReceiver**Call State=" + state);
            Toast.makeText(context, "Phone state: " + state, Toast.LENGTH_SHORT).show();


            SharedPreferences prefs = context.getSharedPreferences("sparksoft.smartguard", Context.MODE_WORLD_READABLE);

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {

                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();

                //Intent navIntent = new Intent(context, SOSMessageActivity.class);
                //navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(navIntent);
                //context.startService(new Intent(context, SmartGuardService.class));


            }
            else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();
                //String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //Log.d(TAG,"PhoneStateReceiver**Incoming call " + incomingNumber);

                //if (!killCall(context)) { // Using the method defined earlier
                    //Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                //}

            }
        }
        else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            //Log.d(TAG,"PhoneStateReceiver **Outgoing call " + outgoingNumber);

            setResultData(null); // Kills the outgoing call

        } else {
            //Log.d(TAG,"PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        }
    }

    public boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            //Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }


}
