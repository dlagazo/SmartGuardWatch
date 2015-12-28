package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MemoryRemindActivity extends Activity {
    int hr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_remind);
        Intent i = getIntent();
        final String filename =  i.getStringExtra("filename");

        Button btnTimeMinus = (Button)findViewById(R.id.btnMemRemindTimeMinus);
        btnTimeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hr > 0)
                {
                    hr--;
                    TextView tv = (TextView)findViewById(R.id.tvMemRemindHours);
                    tv.setText(hr + " hours");
                }
            }
        });

        Button btnTimePlus = (Button)findViewById(R.id.btnMemRemindTimePlus);
        btnTimePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    hr++;
                    TextView tv = (TextView)findViewById(R.id.tvMemRemindHours);
                    tv.setText(hr + " hours");

            }
        });


        Button btnNo = (Button)findViewById(R.id.btnMemTimeNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Button btnYes = (Button)findViewById(R.id.btnMemTimeYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new Thread()
                {
                    public void run() {



                            try{
                                Thread.sleep(1000*60*60*hr);
                                Intent memIntent = new Intent(getApplicationContext(), MemoryPlayActivity.class);
                                memIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                memIntent.putExtra("filename", filename);
                                startActivity(memIntent);
                            }
                            catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }



                    }

                }.start();
                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_remind, menu);
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