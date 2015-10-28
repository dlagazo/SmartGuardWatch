package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Features.VoiceRecognition;
import com.android.sparksoft.smartguardwatch.Models.Contact;

import java.util.ArrayList;

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
        setContentView(R.layout.rect_activity_nav);


        dsContacts = new DataSourceContacts(this);
        dsContacts.open();



        arrayContacts = dsContacts.getAllContacts();

        LinearLayout ll = (LinearLayout)findViewById(R.id.llNav);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ll.getWidth(), ll.getHeight()/5);
        for (final Contact con:dsContacts.getAllContacts())
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
                    finish();
                }
            });
        }

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
