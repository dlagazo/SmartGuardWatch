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


    TelephonyManager telephony;

    public void onReceive(Context context, Intent intent) {
        CallListener phoneListener = new CallListener(context);
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, CallListener.LISTEN_CALL_STATE);
    }

    public void onDestroy() {
        telephony.listen(null, CallListener.LISTEN_NONE);
    }


}
