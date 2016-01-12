package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Models.Constants;

public class InactivityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactivity);

        final SpeechBot sp = new SpeechBot(this, "");

        Button btnInactivityOk = (Button)findViewById(R.id.btnInactivityOk);
        btnInactivityOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(
                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(Constants.INACTIVE_COUNTER_SUCCESSIVE,0).apply();
                prefs.edit().putBoolean(Constants.INACTIVITY_ALARM, false).apply();
                finish();
            }
        });

        Button btnInactivitySOS = (Button)findViewById(R.id.btnInactivitySOS);
        btnInactivitySOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.talk("Are you ok?", true);
                Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SharedPreferences prefs = getSharedPreferences(
                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(Constants.INACTIVE_COUNTER_SUCCESSIVE,0).apply();
                prefs.edit().putBoolean(Constants.INACTIVITY_ALARM, false).apply();

                Intent fallIntent = new Intent(getApplicationContext(), SOSActivity.class);
                fallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(fallIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inactivity, menu);
        return true;
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
}
