package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.AlarmUtils;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Services.ChargingService;
import com.android.sparksoft.smartguardwatch.Services.FallService;
import com.android.sparksoft.smartguardwatch.Services.LocationSensorService;
import com.android.sparksoft.smartguardwatch.Services.SmartGuardService;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenuActivity extends Activity {

    private SpeechBot sp;
    private TextView mTextView;
    private boolean mem = true;
    private Uri fileUri;
    ArrayList<Contact> arrayContacts;
    private boolean navigationAlarm;

    private DataSourceContacts dsContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(
        //        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        //        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );

                //WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
         //       "MyWakelockTag");
        //wakeLock.acquire();
        navigationAlarm = false;

        setContentView(R.layout.rect_activity_menu);
        startListeningtoCalls();
        SharedPreferences prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        //SOS status, 0-inactive, 1-active
        prefs.edit().putInt("sparksoft.smartguard.sos", 0).apply();


        new Thread() {
            public void run() {


                    while (true)
                    {
                        try{
                            Thread.sleep(30000);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable()
                        {

                            @Override
                            public void run() {

                                SharedPreferences prefs = getSharedPreferences(
                                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                                boolean isUserHome = prefs.getBoolean(Constants.IS_USER_AT_HOME, true);
                                if (!isUserHome) {

                                    Button btnNav = (Button) findViewById(R.id.btnNAV);
                                    //btnNav.setVisibility(Button.VISIBLE);
                                    btnNav.setEnabled(true);
                                    btnNav.setBackgroundResource(R.drawable.nav);

                                    if(!navigationAlarm) {
                                        sp.talk("Do you need help with navigation?", true);
                                        Toast.makeText(getApplicationContext(), "Do you need help with navigation", Toast.LENGTH_LONG).show();
                                        navigationAlarm = true;
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        promptSpeechInput();
                                    }
                                }
                                else
                                {

                                    Button btnNav = (Button) findViewById(R.id.btnNAV);
                                    //btnNav.setVisibility(Button.INVISIBLE);
                                    btnNav.setEnabled(false);
                                    btnNav.setBackgroundColor(Color.RED);
                                    navigationAlarm = false;
                                }
                            }
                        });

                }

            }
        }.start();


        //start the fall service


        //dsContacts = new DataSourceContacts(this);
        //dsContacts.open();

        dsContacts = new DataSourceContacts(this);
        dsContacts.open();




        arrayContacts = dsContacts.getAllContacts();

        sp = new SpeechBot(this, null);
        //arrayContacts = dsContacts.getAllContacts();

        Button btnSOS = (Button)findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_SHORT).show();
                sp.talk("Are you ok?", false);


                Intent navIntent = new Intent(getApplicationContext(), SOSActivity.class);

                navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(navIntent);
            }
        });

        Button btnCall = (Button)findViewById(R.id.btnCOM);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Who do you want to call?", Toast.LENGTH_SHORT).show();
                sp.talk("Who do you want to call?", false);




                Intent navIntent = new Intent(getApplicationContext(), ComActivity.class);
                navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(navIntent);
            }
        });

        Button btnNav = (Button)findViewById(R.id.btnNAV);

        btnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Where do you want to go?", Toast.LENGTH_SHORT).show();
                sp.talk("Where do you want to go?", false);




                Intent navIntent = new Intent(getApplicationContext(), NavigateActivity.class);
                navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(navIntent);
            }
        });

        Button btnMem = (Button)findViewById(R.id.btnMEM);
        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                sp.talk("If you want to record a new message, or set a reminder, say record. If you want to listen to an existing message, say listen", false);
                Toast.makeText(getApplicationContext(), "If you want to record a new message or set a reminder say  record. If you want to listen to an existing message say listen", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent memIntent = new Intent(getApplicationContext(), MemoryActivity.class);
                memIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(memIntent);


                /*
                Log.i("CALL_LOG", "Call retrive method worked");
                StringBuffer sb = new StringBuffer();
                Uri contacts = CallLog.Calls.CONTENT_URI;
                Cursor managedCursor = getContentResolver().query(
                        contacts, null, null, null, null);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
                if( managedCursor.moveToLast()   == true ) {
                    String phNumber = managedCursor.getString( number );
                    String callDuration = managedCursor.getString( duration1 );
                    String dir = null;
                    sb.append( "\nPhone Number:--- "+phNumber +" \nCall duration in sec :--- "+callDuration );
                    sb.append("\n----------------------------------");
                    Log.d("CALL_LOG","Call Duration is:-------"+sb);
                }
                managedCursor.close();

                Intent camIntent = new Intent(getApplicationContext(), CameraActivity.class);
                camIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(camIntent);

            */
            }




        });

        Button btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {"Logout", "Sync", "Activity Check", "Update"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle("Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if (which == 0) {
                            //LOGGING OUT
                            sp.talk("Logging out", false);
                            SharedPreferences prefs = getSharedPreferences(
                                    "sparksoft.smartguard", Context.MODE_PRIVATE);
                            prefs.edit().putInt("sparksoft.smartguard.status", 0).apply();
                            prefs.edit().putInt(Constants.PREFS_LOGGED_IN, 0).apply();
                            prefs.edit().putInt(Constants.ACTIVE_COUNTER, 0).apply();
                            prefs.edit().putInt(Constants.INACTIVE_COUNTER, 0).apply();
                            prefs.edit().putInt(Constants.FALL_COUNTER, 0).apply();
                            Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
                            //startService(new Intent(getApplicationContext(), FallService.class));
                            stopService(fallIntent);
                            Intent chargingIntent = new Intent(getApplicationContext(), ChargingService.class);

                            stopService(chargingIntent);


                            String alarmString = prefs.getString(Constants.PREFS_ALARM_STING, "");

                            ArrayList<Alarm> alarms = null;
                            try {
                                alarms = AlarmUtils.parseAlarmString(alarmString);
                                AlarmUtils.cancelAllAlarms(getApplicationContext(), alarms);
                                //AlarmUtils.startAllAlarms(getApplicationContext(), alarms);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*
                            String alarmString = prefs.getString(Constants.PREFS_ALARM_STING, "");
                            ArrayList<Alarm> alarms = null;
                            try {
                                alarms = Alarm.parseAlarmString(alarmString);
                                Alarm.cancelAllAlarms(getApplicationContext(), alarms);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            */

                            finish();

                        } else if (which == 1) {
                            //SYNCHING
                            SharedPreferences prefs = getSharedPreferences(
                                    Constants.PREFS_NAME, Context.MODE_PRIVATE);
                            String auth = prefs.getString("sparksoft.smartguard.auth", "");
                            if (auth.length() > 1) {
                                HelperLogin hr = new HelperLogin(getApplicationContext(), auth, sp);

                                hr.Sync(Constants.URL_LOGIN);


                            }
                        } else if (which == 2) {
                            //ACTIVITY CHECK
                            Intent intent = new Intent(getApplicationContext(), FitnessActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                        } else if (which == 1000000) {
                            //ACTIVITY CHECK
                            Intent intent = new Intent(getApplicationContext(), FitminutesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                        } else if (which == 3) {
                            HelperLogin hr = new HelperLogin(getApplicationContext(), "" , sp);
                            hr.Update("");
                        }
                    }
                });
                builder.show();

            }
        });
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Android File Upload");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private void promptSpeechInput() {



                //Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();

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
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Your device does not support speech to text.",
                            Toast.LENGTH_SHORT).show();
                }
    }



    public void startListeningtoCalls()
    {
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                //Toast.makeText(getApplicationContext(), "Phone listener started", Toast.LENGTH_LONG).show();
                String stateString = "N/A";

                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                Intent chargingService = new Intent(getApplicationContext(), ChargingService.class);
                Intent fallService = new Intent(getApplicationContext(), FallService.class);
                Intent navService = new Intent(getApplicationContext(), LocationSensorService.class);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        //IDLE - 0
                        Log.d("CALL_LOG", "Idle");
                        prefs.edit().putInt(Constants.PREFS_CALL_STATUS, 0).apply();
                        boolean chargeStatus = prefs.getBoolean(Constants.PREFS_CHARGE_STATUS, false);

                        if(chargeStatus)
                        {
                            stopService(chargingService);
                            startService(chargingService);
                        }
                        else
                        {
                            stopService(fallService);
                            startService(fallService);
                            stopService(navService);
                            startService(navService);
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d("CALL_LOG", "Ofhook");
                        stopService(navService);
                        stopService(fallService);
                        stopService(chargingService);
                        prefs.edit().putInt(Constants.PREFS_CALL_STATUS, 1).apply();

                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d("CALL_LOG", "Ringing");
                        prefs.edit().putInt(Constants.PREFS_CALL_STATUS, 2).apply();

                        stopService(navService);
                        stopService(fallService);
                        stopService(chargingService);
                        break;
                    default:
                        Log.d("CALL LOG", "DEFAULT");
                        break;
                }

            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Intent fallService = new Intent(getApplicationContext(), FallService.class);
        stopService(fallService);
        startService(fallService);

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
                        Intent navIntent = new Intent(getApplicationContext(), NavigateActivity.class);
                        navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        sp.talk("Where do you want to go?", true);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        startActivity(navIntent);

                    }

                    else if(result.get(0).toLowerCase().equals("no"))
                    {



                    }
                }
                break;
            }

        }
    }



}
