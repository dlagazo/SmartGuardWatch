package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;



public class ChargeActivity extends Activity {

    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                int chargeStatus = prefs.getInt("sparksoft.smartguard.chargeStatus", -1);
                if(chargeStatus == 0)
                    finish();

            }

        }, 0, 5000);

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                DecimalFormat df = new DecimalFormat("00.00");
                                TextView tvBattery = (TextView)findViewById(R.id.tvBatteryLevel);

                                tvBattery.setText(df.format(getBatteryLevel()) + "%");
                                if(getBatteryLevel() == 100.0f)
                                {
                                    LinearLayout ll = (LinearLayout)findViewById(R.id.llChargeScreen);
                                    ll.setBackgroundColor(Color.BLUE);
                                    TextView tvChargeText = (TextView)findViewById(R.id.tvChargeText);
                                    tvChargeText.setText("Fully Charged. Thank you. Please put me back on.");
                                }
                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_charge, menu);
        return true;
    }

    public float getBatteryLevel() {
        Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
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
