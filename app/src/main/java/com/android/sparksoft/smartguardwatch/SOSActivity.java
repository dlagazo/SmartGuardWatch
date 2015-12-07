package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Features.VoiceRecognition;
import com.android.sparksoft.smartguardwatch.Listeners.CallListener;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SOSActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private VoiceRecognition vr;
    private Timer myTimer;
    private SpeechBot sp;
    private boolean isOk = false;
    private ArrayList<Contact> arrayContacts;
    private DataSourceContacts dsContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        sp = new SpeechBot(this, "");
        dsContacts = new DataSourceContacts(this);
        dsContacts.open();




        arrayContacts = dsContacts.getAllContacts();
        setContentView(R.layout.activity_sos);

        Button btnSOSCall = (Button)findViewById(R.id.btnSOSCall);
        btnSOSCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTimer.purge();
                myTimer.cancel();
                isOk = true;
                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                sp.talk("Emergency protocol is initiated. Smart guard will now call your contacts", true);
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }




                for (Contact cont:arrayContacts)
                {
                    if(cont.getType() == 0 && cont.canCall())
                    {
                        int status = prefs.getInt("sparksoft.smartguard.sosCallStatus", 0);


                        if(status == 0)
                        {
                            sp.talk("Calling " + cont.getFullName(), true);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cont.getMobile()));
                            startActivity(intent);
                            try {
                                Thread.sleep(30000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            break;
                        }

                    }
                    //Toast.makeText(getApplicationContext(), "Calling " + arrayContacts.get(0).getFullName(), Toast.LENGTH_SHORT).show();

                    //CallListener caller = new CallListener(getApplicationContext(), sp, arrayContacts);


                }

                finish();
            }
        });

        Button btnSOSOk = (Button)findViewById(R.id.btnSOSOk);
        btnSOSOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();

                myTimer.purge();
                myTimer.cancel();
                isOk = true;
                finish();
            }
        });

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                promptSpeechInput();

            }

        }, 0, 10000);




    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sp.destroy();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int code) {
        if (code == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.ENGLISH);


        } else {
            tts = null;
            Toast.makeText(this, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void promptSpeechInput() {

        for(int i = 0; i < 3; i++)
        {
            if(!isOk)
            {
                //Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();
                sp.talk("Are you ok?", true);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Say something");
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Your device does not support speech to text.",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "Do you need an emergency call?", Toast.LENGTH_SHORT).show();
                if(i==2)
                {
                    myTimer.purge();
                    myTimer.cancel();
                    isOk = true;
                    SharedPreferences prefs = getSharedPreferences(
                            "sparksoft.smartguard", Context.MODE_PRIVATE);
                    prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                    sp.talk("Emergency protocol is initiated. Smart guard will now call your contacts", true);
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    for (Contact cont:arrayContacts) {
                        int callStatus = prefs.getInt(Constants.PREFS_CALL_STATUS, 0);
                        if(callStatus == 0)
                        {
                            sp.talk("Calling " + cont.getFullName(), true);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cont.getMobile()));
                            startActivity(callIntent);
                            prefs.edit().putInt(Constants.PREFS_CALL_STATUS, 1).apply();

                        }
                        else{
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    //Toast.makeText(getApplicationContext(), "Calling " + arrayContacts.get(0).getFullName(), Toast.LENGTH_SHORT).show();

                    //CallListener caller = new CallListener(getApplicationContext(), sp, arrayContacts);

                    finish();
                }
            }

        }

    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_SHORT).show();
                    //txtSpeechInput.setText(result.get(0));
                    if(result.get(0).toLowerCase().equals("yes") || result.get(0).toLowerCase().equals("ok"))
                    {
                        SharedPreferences prefs = getSharedPreferences(
                                "sparksoft.smartguard", Context.MODE_PRIVATE);
                        prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                        myTimer.purge();
                        myTimer.cancel();
                        isOk = true;
                        finish();
                    }

                    else if(result.get(0).toLowerCase().equals("no"))
                    {
                        myTimer.purge();
                        myTimer.cancel();
                        isOk = true;
                        SharedPreferences prefs = getSharedPreferences(
                                "sparksoft.smartguard", Context.MODE_PRIVATE);
                        prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                        sp.talk("Emergency protocol is initiated. Smart guard will now call your contacts", true);
                        try {
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                        for (Contact cont:arrayContacts) {
                            int status = prefs.getInt("sparksoft.smartguard.sosCallStatus", 0);
                            if(status == 0)
                            {
                                sp.talk("Calling " + cont.getFullName(), true);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cont.getMobile()));
                                startActivity(intent);
                                try {
                                    Thread.sleep(30000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                break;
                            }

                        }
                        //Toast.makeText(getApplicationContext(), "Calling " + arrayContacts.get(0).getFullName(), Toast.LENGTH_SHORT).show();

                        //CallListener caller = new CallListener(getApplicationContext(), sp, arrayContacts);

                        finish();
                    }
                }
                break;
            }

        }
    }

}
