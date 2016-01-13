package com.android.sparksoft.smartguardwatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;

import java.util.ArrayList;

public class DeviceLostActivity extends Activity {

    private ArrayList<Contact> arrayContacts;
    private DataSourceContacts dsContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_lost);

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
                SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);


                int deviceStatus = prefs.getInt("deviceStatus", 0);

                if(deviceStatus == 0)
                {
                    finish();
                }
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
