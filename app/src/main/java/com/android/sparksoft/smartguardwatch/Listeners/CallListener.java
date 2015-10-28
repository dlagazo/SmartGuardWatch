package com.android.sparksoft.smartguardwatch.Listeners;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

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
    private boolean didRing;
    private ArrayList<Contact> contacts;
    private int callCount;
    private SharedPreferences sharedPrefs;

    public CallListener(Context _context, SpeechBot _sp, ArrayList<Contact> _contacts)
    {
        callCount = 0;
        context = _context;
        sp = new SpeechBot(context, null);
        didHook = false;
        didRing = false;
        contacts = _contacts;
        sharedPrefs = _context.getSharedPreferences("prefs", Context.MODE_WORLD_WRITEABLE);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contacts.get(0).getMobile()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        if(TelephonyManager.CALL_STATE_RINGING == state) {
            //Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            Toast.makeText(context, "Call state is ringing.", Toast.LENGTH_LONG).show();

            editor.putString("callState", "ringing");
            didRing = true;
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            Toast.makeText(context, "Call state is offhook.", Toast.LENGTH_LONG).show();
            editor.putString("callState", "offhook");
            didHook = true;
            //sp.talk("Phone is off-hook", true);

        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            Toast.makeText(context, "Call state is idle.", Toast.LENGTH_LONG).show();
            if(!didRing && didHook)
            {
                callCount++;
                if(callCount < contacts.size())
                {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contacts.get(callCount).getMobile()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    Intent voiceintent = new Intent();
                    voiceintent.setAction(Intent.ACTION_VIEW);
                    voiceintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    File file = new File("/storage/emulated/0/Samsung/Music/Over the Horizon.mp3");
                    voiceintent.setDataAndType(Uri.fromFile(file), "audio/*");
                    context.startActivity(voiceintent);
                }


                /*
                String mobile = contacts.get(0).getMobile();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                */

            }
        }

    }
}
