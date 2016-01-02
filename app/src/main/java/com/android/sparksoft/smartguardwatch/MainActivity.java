package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Services.FallService;
import com.android.sparksoft.smartguardwatch.Services.LocationSensorService;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private SpeechBot sp;
    private TextView mTextView;
    private HelperLogin hr;
    private Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.rect_activity_login);

        sp = new SpeechBot(this, null);

        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        int status = prefs.getInt("sparksoft.smartguard.status", 0);
        prefs.edit().putInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY, 1).apply();

        if (status == 1) {
            Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
            //startService(new Intent(getApplicationContext(), FallService.class));
            stopService(fallIntent);
            startService(fallIntent);

            Intent locationIntent = new Intent(getApplicationContext(), LocationSensorService.class);

            stopService(locationIntent);
            startService(locationIntent);


            finish();
        }

        final EditText etUserName = (EditText)findViewById(R.id.etUsername);
        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });




        final Button btnLogIn = (Button)findViewById(R.id.btnLogin);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Logging in. Please wait", true);

                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                prefs.edit().putInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY, 1).apply();
                prefs.edit().putInt(Constants.PREFS_CALL_STATUS, 0).apply();
                prefs.edit().putInt(Constants.PREFS_LOGGED_IN, 1).apply();
                prefs.edit().putInt(Constants.PREFS_SOS_CALL_STATUS, 0).apply();
                prefs.edit().putInt("sparksoft.smartguard.sosDidAnswer", 0).apply();
                prefs.edit().putBoolean(Constants.IS_USER_AT_HOME, true).apply();
                prefs.edit().putBoolean(Constants.INACTIVITY_ALARM, false).apply();


                int status = prefs.getInt("sparksoft.smartguard.status", 0);

                if (status == 1) {
                    Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
                    //startService(new Intent(getApplicationContext(), FallService.class));
                    stopService(fallIntent);
                    startService(fallIntent);


                    finish();
                } else if (status == 0) {
                    Toast.makeText(getApplicationContext(), "Logging in. Please wait.", Toast.LENGTH_LONG).show();

                    final String basicAuth = "Basic " + Base64.encodeToString((etUserName.getText() + ":" +
                            etPassword.getText()).getBytes(), Base64.NO_WRAP);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    hr = new HelperLogin(getApplicationContext(), basicAuth, sp);
                    hr.SyncHelperJSONObject(Constants.URL_LOGIN);
                    myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            checkLoginStatus();

                        }

                    }, 0, 5000);
                } else {
                    //Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
                    //startService(new Intent(getApplicationContext(), FallService.class));
                    //stopService(fallIntent);
                    //startService(fallIntent);
                    Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
                    //startService(new Intent(getApplicationContext(), FallService.class));
                    prefs.edit().putInt(Constants.PREFS_SOS_PROTOCOL_ACTIVITY, 1).apply();
                    stopService(fallIntent);
                    startService(fallIntent);
                    finish();
                }
            }
        });
        /*
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        */
    }

    public void checkLoginStatus()
    {
        SharedPreferences prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);

        int status = prefs.getInt(Constants.PREFS_LOG_CHECKER, 0);
        if(status == 1)
        {

            Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
            //startService(new Intent(getApplicationContext(), FallService.class));
            stopService(fallIntent);
            startService(fallIntent);
            Intent locationIntent = new Intent(getApplicationContext(), LocationSensorService.class);

            stopService(locationIntent);
            startService(locationIntent);





            myTimer.purge();
            myTimer.cancel();
            prefs.edit().putInt(Constants.PREFS_LOG_CHECKER, 0).apply();
            finish();

        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
