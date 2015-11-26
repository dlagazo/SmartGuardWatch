package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
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
    private SharedPreferences sharedPrefs;
    TelephonyManager telephonyManager;

    public CallListener(Context _context)
    {
        callCount = 0;
        context = _context;
        //sp = new SpeechBot(context, null);
        didHook = false;
        didRing = false;
        isOver = false;
        dsContacts = new DataSourceContacts(context);
        dsContacts.open();




        contacts = dsContacts.getAllContacts();
        sharedPrefs = _context.getSharedPreferences("prefs", Context.MODE_WORLD_WRITEABLE);
        //telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
        //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contacts.get(0).getMobile()));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(intent);

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        SharedPreferences prefs = context.getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        if(TelephonyManager.CALL_STATE_RINGING == state) {
            //Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            Toast.makeText(context, "Call state is ringing.", Toast.LENGTH_LONG).show();


            didRing = true;
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            //Toast.makeText(context, "Call state is offhook.", Toast.LENGTH_LONG).show();

            didHook = true;

            //sp.talk("Phone is off-hook", true);

        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            //Toast.makeText(context, "Call count:" + callCount + " Contacts:" + contacts.size(), Toast.LENGTH_LONG).show();
            if(!didRing && didHook)
            {


                prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 0).apply();




            }
            else if(didRing && didHook) {
                Toast.makeText(context, "Answered", Toast.LENGTH_LONG).show();

            }
        }

    }
}
