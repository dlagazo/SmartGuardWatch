package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourcePlaces;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Features.VoiceRecognition;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Place;

import java.util.ArrayList;
import java.util.Locale;

public class NavigateActivity extends Activity {
    VoiceRecognition vr;
    SpeechBot sp;
    ArrayList<Place> arrayPlaces;
    SharedPreferences prefs;
    private DataSourcePlaces dsPlaces;
    private static final int VOICE_RECOGNITION = 1;
    private LocationManager mLocationManager;
    int status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_nav);



        dsPlaces = new DataSourcePlaces(this);
        dsPlaces.open();


        prefs = getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);


        arrayPlaces = dsPlaces.getAllPlaces();

        LinearLayout ll = (LinearLayout)findViewById(R.id.llNav);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ll.getWidth(), ll.getHeight()/5);

        for (final Place place:dsPlaces.getAllPlaces())
        {
            Button btn = new Button(this);
            //btn.setHeight(0);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    80));
            //btn.setWidth(ll.getWidth());
            btn.setText(place.getPlaceName());

            ll.addView(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=14.672,121.022&daddr="+
                                            place.getPlaceLat() + "," + place.getPlaceLong()));
                    startActivity(intent);
                }
            });
        }
        Button btn = new Button(this);
        //btn.setHeight(0);
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                80));
        //btn.setWidth(ll.getWidth());
        btn.setText("Back");

        ll.addView(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        if(prefs.getBoolean(Constants.PREFS_VR_COMM, true))
            speak();

    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


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

            for(Place place: dsPlaces.getAllPlaces()) {
                for (String str : results.get(0).split(" ")) {
                    if (str.toLowerCase().equals(place.getPlaceName().toLowerCase()))
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=14.672,121.022&daddr="+
                                        place.getPlaceLat() + "," + place.getPlaceLong()));
                        startActivity(intent);
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getResources().getString(R.string.toast_where_to_go));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        startActivityForResult(intent, VOICE_RECOGNITION);
    }




}
