package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Features.VoiceRecognition;
import com.android.sparksoft.smartguardwatch.Models.Contact;

import java.util.ArrayList;
import java.util.Locale;

public class ComActivity extends Activity {

    VoiceRecognition vr;
    //SpeechBot sp;
    ArrayList<Contact> arrayContacts;
    SharedPreferences sharedPrefs;
    private DataSourceContacts dsContacts;
    private static final int VOICE_RECOGNITION = 1;
    int status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.rect_activity_nav);


        dsContacts = new DataSourceContacts(this);
        dsContacts.open();




        arrayContacts = dsContacts.getAllContacts();
        setPrimary();





        speak();
    }

    private void setPrimary()
    {
        LinearLayout ll = (LinearLayout)findViewById(R.id.llNav);
        ll.removeAllViews();
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ll.getWidth(), ll.getHeight()/5);
        for (final Contact con:dsContacts.getAllContacts())
        {
            if(con.getType() == 0)
            {
                Button btn = new Button(this);
                //btn.setHeight(0);
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        0, 1f));
                //btn.setWidth(ll.getWidth());
                btn.setText(con.getFullName());

                ll.addView(btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + con.getMobile()));
                        startActivity(intent);

                        //Intent navIntent = new Intent(getApplicationContext(), SOSMessageActivity.class);
                        //navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(navIntent);
                    }
                });
            }
        }
        Button btnSecondary = new Button(this);
        btnSecondary.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                0, 1f));
        btnSecondary.setText("Secondary Contacts");
        ll.addView(btnSecondary);
        btnSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout)findViewById(R.id.llNav);
                ll.removeAllViews();
                for (final Contact con:dsContacts.getAllContacts())
                {
                    if(con.getType() == 1)
                    {
                        Button btn = new Button(getApplicationContext());
                        //btn.setHeight(0);
                        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                0, 1f));
                        //btn.setWidth(ll.getWidth());
                        btn.setText(con.getFullName());

                        ll.addView(btn);
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + con.getMobile()));
                                startActivity(intent);

                                //Intent navIntent = new Intent(getApplicationContext(), SOSMessageActivity.class);
                                //navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //startActivity(navIntent);
                            }
                        });
                    }
                }
                Button btn = new Button(getApplicationContext());
                //btn.setHeight(0);
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        0, 1f));
                //btn.setWidth(ll.getWidth());
                btn.setText("Back");
                ll.addView(btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });

        Button btn = new Button(this);
        //btn.setHeight(0);
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                0, 1f));
        //btn.setWidth(ll.getWidth());
        btn.setText("Back");

        ll.addView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_com, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        if (requestCode == VOICE_RECOGNITION && resultCode == RESULT_OK) {
            ArrayList<String> results;
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // TODO Do something with the recognized voice strings

            Toast.makeText(this, results.get(0), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), results.get(0), Toast.LENGTH_LONG).show();
            for(Contact con:dsContacts.getAllContacts()) {
                for (String str : results.get(0).split(" ")) {
                    if (str.toLowerCase().equals(con.getFirstName().toLowerCase()))
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + con.getMobile()));
                        startActivity(intent);
                        finish();
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify free form input
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please start speaking");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, VOICE_RECOGNITION);
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
