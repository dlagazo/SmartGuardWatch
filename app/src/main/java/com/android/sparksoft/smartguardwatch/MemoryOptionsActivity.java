package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MemoryOptionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_options);
        Intent i = getIntent();
        final String filename =  i.getStringExtra("filename");

        Button btnMsg = (Button)findViewById(R.id.memMsg);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnRemind = (Button)findViewById(R.id.memRemind);
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent memIntent = new Intent(getApplicationContext(), MemoryRemindActivity.class);
                memIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                memIntent.putExtra("filename", filename);
                startActivity(memIntent);
                finish();
            }
        });


        Button btnListen = (Button)findViewById(R.id.memListenAgain);
        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MediaPlayer mediaPlayer = new  MediaPlayer();
                try {
                    mediaPlayer.setDataSource(filename);
                    mediaPlayer.prepare();
                    Toast.makeText(getApplicationContext(), "Playing memory record", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_options, menu);
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
