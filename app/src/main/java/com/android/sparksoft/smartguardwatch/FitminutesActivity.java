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

public class FitminutesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitminutes);
        final SharedPreferences prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        int fitminutes = prefs.getInt(Constants.FITMINUTES_COUNTER, 0);
        int fitminutesDuration = prefs.getInt(Constants.PREFS_FITMINUTE_DURATION, 150);
        double fitminutesPct = (double)fitminutes/fitminutesDuration;

        SeekBar sb = (SeekBar)findViewById(R.id.sbFitminsPct);
        sb.setMax(fitminutesDuration);
        sb.setProgress(fitminutes);

        DecimalFormat df = new DecimalFormat("00.00");
        TextView tvFitmins = (TextView)findViewById(R.id.tvFitminsStatus);
        tvFitmins.setText(fitminutes + "/" +
                        fitminutesDuration + "mins  " + df.format(fitminutesPct*100) + "% completed");

        Button btnRemind = (Button)findViewById(R.id.btnFitminsRemind);
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitminutes, menu);
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
