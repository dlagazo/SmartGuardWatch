package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.sparksoft.smartguardwatch.Services.FallService;

public class MenuActivity extends Activity {

    private SpeechBot sp;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_menu);

        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        //SOS status, 0-inactive, 1-active
        prefs.edit().putInt("sparksoft.smartguard.sos", 0).apply();

        //start the fall service
        startService(new Intent(getApplicationContext(), FallService.class));


        //dsContacts = new DataSourceContacts(this);
        //dsContacts.open();



        sp = new SpeechBot(this, null);
        //arrayContacts = dsContacts.getAllContacts();

        Button btnSOS = (Button)findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Do you need an emergency call?", false);
                Toast.makeText(getApplicationContext(), "Do you need an emergency call?", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent navIntent = new Intent(getApplicationContext(), SOSActivity.class);

                navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(navIntent);
            }
        });

        Button btnCall = (Button)findViewById(R.id.btnCOM);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Who do you want to call?", false);
                Toast.makeText(getApplicationContext(), "Who do you want to call?", Toast.LENGTH_SHORT).show();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent navIntent = new Intent(getApplicationContext(), ComActivity.class);
                navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(navIntent);
            }
        });

        Button btnNav = (Button)findViewById(R.id.btnNAV);
        btnNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Where do you want to go?", false);

                Toast.makeText(getApplicationContext(), "Where do you want to go?", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent navIntent = new Intent(getApplicationContext(), NavigateActivity.class);
                navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(navIntent);
            }
        });

        Button btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {"Logout", "Sync"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle("Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if (which == 0) {
                            SharedPreferences prefs = getSharedPreferences(
                                    "sparksoft.smartguard", Context.MODE_PRIVATE);
                            prefs.edit().putInt("sparksoft.smartguard.status", 0).apply();
                            finish();
                            sp.talk("Logging out", false);
                        } else if (which == 1) {
                            SharedPreferences prefs = getSharedPreferences(
                                    "sparksoft.smartguard", Context.MODE_PRIVATE);
                            String auth = prefs.getString("sparksoft.smartguard.auth", "");
                            if (auth.length() > 1) {
                                HelperLogin hr = new HelperLogin(getApplicationContext(), auth, sp);
                                String url = "http://smartguardwatch.azurewebsites.net/api/MobileContact";
                                hr.Sync(url);
                                sp.talk("Data synching complete", false);
                                Toast.makeText(getApplicationContext(), "Data syncing complete.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.show();

            }
        });
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
