package com.android.sparksoft.smartguardwatch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Services.FallService;

import java.util.ArrayList;

public class DeviceLostActivity extends Activity {

    private ArrayList<Contact> arrayContacts;
    private DataSourceContacts dsContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_lost);

        Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
        stopService(fallIntent);

        dsContacts = new DataSourceContacts(this);
        dsContacts.open();






        arrayContacts = dsContacts.getAllContacts();


        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        TextView tvSpiel = (TextView)findViewById(R.id.tvLostMessage);
        tvSpiel.setText(prefs.getString("MissingSpiel", ""));

        Button btnFound = (Button)findViewById(R.id.btnFound);
        btnFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please input your password to unlock the device", Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceLostActivity.this);
                builder.setTitle("Input password");

// Set up the input
                final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = "";
                        password = input.getText().toString();

                        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                        String realPassword = prefs.getString("realPassword", "");

                        //int deviceStatus = prefs.getInt("deviceStatus", 0);

                        if (password.equals(realPassword)) {
                            prefs.edit().putInt("deviceStatus", 0).apply();
                            Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
                            //startService(new Intent(getApplicationContext(), FallService.class));
                            stopService(fallIntent);
                            startService(fallIntent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry, wrong password. Please try again.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();



            }
        });

        Button btnLostCall = (Button)findViewById(R.id.btnLostCall);
        btnLostCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + arrayContacts.get(0).getMobile()));
                startActivity(intent);
            }
        });




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {

        }
        return (keyCode == KeyEvent.KEYCODE_BACK ? true : super.onKeyDown(keyCode, event));
    }

}
