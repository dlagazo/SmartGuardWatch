package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.SurfaceView;
import android.view.View;
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
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Services.ChargingService;
import com.android.sparksoft.smartguardwatch.Services.FallService;
import com.android.sparksoft.smartguardwatch.Services.SmartGuardService;

import org.json.JSONException;

import java.io.File;
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


        setContentView(R.layout.rect_activity_menu);

        SharedPreferences prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        //SOS status, 0-inactive, 1-active
        prefs.edit().putInt("sparksoft.smartguard.sos", 0).apply();

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
        /*
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
        */
        Button btnMem = (Button)findViewById(R.id.btnMEM);
        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                Intent memIntent = new Intent(getApplicationContext(), MemoryActivity.class);
                memIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(memIntent);


            }
        });

        Button btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {"Logout", "Sync", "Activity Check"};

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
                            prefs.edit().putInt(Constants.ACTIVE_COUNTER,0).apply();
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
                                alarms = Alarm.parseAlarmString(alarmString);
                                Alarm.cancelAllAlarms(getApplicationContext(), alarms);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




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
}
