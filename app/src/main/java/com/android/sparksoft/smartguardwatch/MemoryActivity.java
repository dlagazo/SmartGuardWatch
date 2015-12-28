package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        final SpeechBot sp = new SpeechBot(this, "");

        Button btnMemExit = (Button)findViewById(R.id.btnMemExit);
        btnMemExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnMemListen = (Button)findViewById(R.id.btnListen);
        btnMemListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> files = getListFiles(new File("/sdcard/smartguard"));
                if(files.size() == 0)
                {
                    Toast.makeText(getApplication(), "You have no memory records", Toast.LENGTH_LONG).show();
                    sp.talk("You have no memory records", true);
                }
                for (File file:files) {
                    Log.d("MEMORIES", file.getPath());
                    Intent memIntent = new Intent(getApplicationContext(), MemoryPlayActivity.class);
                    memIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    memIntent.putExtra("filename", file.getPath());
                    startActivity(memIntent);
                }

            }
        });

        Button btnRecord = (Button)findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaRecorder recorder = new MediaRecorder();
                ContentValues values = new ContentValues(3);
                final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                        .format(new Date());
                values.put(MediaStore.MediaColumns.TITLE, "Memory");
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                File folder = new File("/sdcard/smartguard");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }

                recorder.setOutputFile("/sdcard/smartguard/MEMORY_" + timeStamp + ".mp3");
                try {
                    recorder.prepare();
                } catch (Exception e){
                    e.printStackTrace();
                }

                final ProgressDialog mProgressDialog = new ProgressDialog(MemoryActivity.this);
                mProgressDialog.setTitle("Recording Memory");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mProgressDialog.dismiss();
                        recorder.stop();
                        recorder.release();
                        Intent memIntent = new Intent(getApplicationContext(), MemoryOptionsActivity.class);
                        memIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        memIntent.putExtra("filename", "/sdcard/smartguard/MEMORY_" + timeStamp + ".mp3");
                        startActivity(memIntent);
                    }
                });

                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                    public void onCancel(DialogInterface p1) {
                        recorder.stop();
                        recorder.release();
                    }
                });
                recorder.start();
                mProgressDialog.show();
            }
        });
    }



    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".mp3")){
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory, menu);
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
