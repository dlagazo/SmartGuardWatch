package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.sparksoft.smartguardwatch.Models.Constants;

import java.text.DecimalFormat;

public class FitnessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);
        final SharedPreferences prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        int active = prefs.getInt(Constants.ACTIVE_COUNTER, 0);
        int inactive = prefs.getInt(Constants.INACTIVE_COUNTER, 0);
        int fall = prefs.getInt(Constants.FALL_COUNTER, 0);

        double total = active + inactive;
        double activePct = active/total;
        double inactivePct = inactive/total;

        TextView tvFall = (TextView)findViewById(R.id.fallCount);
        tvFall.setText("Fall count: " + Integer.toString(fall));
        DecimalFormat df = new DecimalFormat("00.00");
        TextView tvActive = (TextView)findViewById(R.id.activeCount);
        tvActive.setText("Active " + df.format(activePct * 100) + "% of the day");

        TextView tvInactive = (TextView)findViewById(R.id.inactiveCount);
        tvInactive.setText("Inactive " + df.format(inactivePct * 100) + "% of the day");



        Button btnReset = (Button)findViewById(R.id.btnResetCounters);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putInt(Constants.FALL_COUNTER, 0).apply();
                prefs.edit().putInt(Constants.ACTIVE_COUNTER, 0).apply();
                prefs.edit().putInt(Constants.INACTIVE_COUNTER, 0).apply();
                finish();
            }
        });

        Button btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



        TextView tvFut = (TextView)findViewById(R.id.fut);
        tvFut.setText("Fall Upper Threshold: " +
                prefs.getString(Constants.PREFS_FALL_PARAMETER_FUT, Double.toString(Constants.FALL_THRESHOLD)));

        TextView tvPut = (TextView)findViewById(R.id.put);
        tvPut.setText("Peak Upper Threshold: " +
                prefs.getInt(Constants.PREFS_FALL_PARAMETER_PUT, Constants.UPPER_LIMIT_PEAK_COUNT));

        TextView tvPlt = (TextView)findViewById(R.id.plt);
        tvPlt.setText("Peak Lower Threshold: " +
                prefs.getInt(Constants.PREFS_FALL_PARAMETER_PLT, Constants.LOWER_LIMIT_PEAK_COUNT));

        TextView tvRmt = (TextView)findViewById(R.id.rmt);
        tvRmt.setText("Residual Movement Threshold: " +
                prefs.getString(Constants.PREFS_FALL_PARAMETER_RMT, Double.toString(Constants.MOVE_THRESHOLD)));

        TextView tvFwd = (TextView)findViewById(R.id.fwd);
        tvFwd.setText("Fall Window Duration: " +
                prefs.getString(Constants.PREFS_FALL_PARAMETER_FWD, Double.toString(Constants.FALL_DETECT_WINDOW_SECS)));

        TextView tvRwd = (TextView)findViewById(R.id.rwd);
        tvRwd.setText("Residual Window Duration: " +
                prefs.getString(Constants.PREFS_FALL_PARAMETER_RMD, Double.toString(Constants.VERIFY_FALL_DETECT_WINDOW_SECS)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitness, menu);
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
