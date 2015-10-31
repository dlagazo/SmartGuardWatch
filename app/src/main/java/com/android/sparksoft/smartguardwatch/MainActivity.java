package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;

public class MainActivity extends Activity {

    private SpeechBot sp;
    private TextView mTextView;
    private HelperLogin hr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_login);

        sp = new SpeechBot(this, null);

        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();

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


        final int status = prefs.getInt("sparksoft.smartguard.status", 0);

        if(status == 1)
        {
            Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        }

        final Button btnLogIn = (Button)findViewById(R.id.btnLogin);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Logging in. Please wait", false);



                if(status == 0) {
                    Toast.makeText(getApplicationContext(), "Logging in. Please wait.", Toast.LENGTH_LONG).show();

                    String url = "http://smartguardwatch.azurewebsites.net/api/MobileContact";


                    final String basicAuth = "Basic " + Base64.encodeToString((etUserName.getText() + ":" +
                            etPassword.getText()).getBytes(), Base64.NO_WRAP);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    hr = new HelperLogin(getApplicationContext(), basicAuth, sp);
                    hr.SyncHelperJSONObject(url);
                }
                else
                {
                    Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
