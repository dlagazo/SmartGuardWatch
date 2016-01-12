package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Daniel on 10/18/2015.
 */
public class CallListener extends PhoneStateListener {

    private Context context;
    private SpeechBot sp;
    private boolean didHook;
    private boolean didRing, isOver;

    private ArrayList<Contact> contacts;
    private DataSourceContacts dsContacts;
    private int callCount;
    private SharedPreferences prefs;
    TelephonyManager telephonyManager;

    public CallListener(Context _context)
    {
        callCount = 0;
        context = _context;
        sp = new SpeechBot(context, null);
        didHook = false;
        didRing = false;
        isOver = false;
        dsContacts = new DataSourceContacts(context);
        dsContacts.open();




        contacts = dsContacts.getAllContacts();
        prefs =  _context.getSharedPreferences(
            "sparksoft.smartguard", Context.MODE_WORLD_READABLE);
        //telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
        //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contacts.get(0).getMobile()));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent);

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if(TelephonyManager.CALL_STATE_RINGING == state) {
            Log.d("CALL_STATUS", "RINGING");
            //Toast.makeText(context, "Call state is ringing.", Toast.LENGTH_LONG).show();
            //prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 1).apply();

            didRing = true;
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            //Toast.makeText(context, "Call state is offhook.", Toast.LENGTH_LONG).show();

            didHook = true;
            prefs.edit().putInt("sparksoft.smartguard.sosDidAnswer", 1).apply();
            //prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 0).apply();
            //sp.talk("Phone is off-hook", true);
            //prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 0).apply();
        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            //Toast.makeText(context, "Call count:" + callCount + " Contacts:" + contacts.size(), Toast.LENGTH_LONG).show();
            /*
            if(!didHook && !didRing)
            {
                Log.d("CALL_STATUS", "BUSY");
                Toast.makeText(context, "Busy", Toast.LENGTH_LONG).show();
                prefs.edit().putInt(Constants.PREFS_CALL_STATUS, 0).apply();
                prefs.edit().putInt("sparksoft.smartguard.sosDidAnswer", 0).apply();
                prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 0).apply();


            }*/
            //int didAnswer = prefs.getInt("sparksoft.smartguard.sosDidAnswer", 0);
            //int sosStatus = prefs.getInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY, 1);
            if(didHook) {

                Log.d("CALL_STATUS", "ANSWERED");
                Toast.makeText(context, "Answered", Toast.LENGTH_LONG).show();
                prefs.edit().putInt("sparksoft.smartguard.sosDidAnswer", 1).apply();
                //prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 0).apply();

            }

            else
            {

                Log.d("CALL_STATUS", "UNANSWERED");
                Toast.makeText(context, "Unanswered", Toast.LENGTH_LONG).show();
                /*
                int last = prefs.getInt("sparksoft.smartguard.lastCalled", 1);
                String numbers = prefs.getString("sparksoft.smartguard.SOSnumbers", "");
                String[] getNumbers = numbers.split(",");
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getNumbers[last]));
                //context.startActivity(intent);
                prefs.edit().putInt("sparksoft.smartguard.lastCalled", last+ 1).apply();
                */
            }
            //prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 0).apply();
        }

    }
}
