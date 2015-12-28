package com.android.sparksoft.smartguardwatch;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.sparksoft.smartguardwatch.Features.VoiceRecognition;
import com.android.sparksoft.smartguardwatch.Helpers.HelperLogin;
import com.android.sparksoft.smartguardwatch.Listeners.CallListener;
import com.android.sparksoft.smartguardwatch.Models.CameraPreview;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Services.FallService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SOSActivity extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private VoiceRecognition vr;
    private Timer myTimer;
    private Timer callTimer;
    private Timer animateSOSTimer;
    private SpeechBot sp;
    private boolean isOk = false;
    private ArrayList<Contact> arrayContacts;
    private DataSourceContacts dsContacts;
    private boolean isAlarmOver = false;
    private ArrayList<Contact> peopleToCall;
    TelephonyManager telephonyManager;

    private Camera mCamera;
    private CameraPreview mCameraPreview;



    private boolean isBlink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_sos);

        sp = new SpeechBot(this, "");
        dsContacts = new DataSourceContacts(this);
        dsContacts.open();

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.sos_camera_preview);
        preview.addView(mCameraPreview);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        arrayContacts = dsContacts.getAllContacts();



        new Thread() {
            public void run() {
                while (true) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Button btnSOSCall = (Button)findViewById(R.id.btnSOSCall);
                                if(!isBlink) {
                                    try {
                                        btnSOSCall.setBackgroundColor(Color.WHITE);
                                    }
                                    catch (Exception ex)
                                    {
                                    }
                                    isBlink = true;
                                }
                                else {

                                    try {
                                        btnSOSCall.setBackgroundColor(Color.RED);
                                    }
                                    catch (Exception ex)
                                    {
                                    }
                                    isBlink = false;
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

        Button btnSOSCall = (Button)findViewById(R.id.btnSOSCall);
        btnSOSCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTimer.purge();
                myTimer.cancel();
                isOk = true;

                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                sp.talk("Emergency protocol is initiated. Smart guard will now call your contacts", true);
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mCamera.takePicture(null, null, mPicture);


                /*
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Looper.prepare();
                        call(prepareContactList());


                    }
                };

                 new Thread(runnable).start();
                 */







            }
        });

        Button btnSOSOk = (Button)findViewById(R.id.btnSOSOk);
        btnSOSOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);
                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();

                myTimer.purge();
                myTimer.cancel();
                isOk = true;
                mCamera.takePicture(null, null, mPicture);
                finish();
            }
        });

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                promptSpeechInput();

            }

        }, 0, 10000);




    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();



                SharedPreferences prefs = getSharedPreferences(
                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                String auth = prefs.getString("sparksoft.smartguard.auth", "");
                if (auth.length() > 1) {
                    HelperLogin hr = new HelperLogin(getApplicationContext(), auth, sp);

                    hr.sendFallData(Constants.URL_FALLDATA, data);


                }


                alarm();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }
    };

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }






    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(

                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "smartguard");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void alarm()
    {
        peopleToCall = prepareContactList();

        new Thread() {
            public void run() {

                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                setContentView(R.layout.rect_activity_medical);

                                final Button btnMedicalData = (Button)findViewById(R.id.btnMedical);
                                btnMedicalData.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent medIntent = new Intent(getApplicationContext(), MedicalDataActivity.class);
                                        medIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(medIntent);
                                    }
                                });

                                final Button btnEnd = (Button)findViewById(R.id.btnEnd);
                                btnEnd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        if(!isAlarmOver)
                                        {

                                            myTimer.purge();
                                            myTimer.cancel();
                                            callTimer.purge();
                                            callTimer.cancel();
                                            sp.talk("Emergency alarm is turned off.", false);
                                            btnEnd.setText("Exit");
                                            isAlarmOver = true;
                                        }
                                        else {
                                            Intent fallIntent = new Intent(getApplicationContext(), FallService.class);
                                            stopService(fallIntent);
                                            startService(fallIntent);
                                            finish();

                                        }

                                    }
                                });

                            }
                        });
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
        }.start();

        callTimer = new Timer();
        callTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                call();

            }

        }, 0, 60000);

    }

    // Simulating something timeconsuming


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sp.destroy();
        try{
            mCamera.release();

            myTimer.purge();
            myTimer.cancel();
            callTimer.purge();
            callTimer.cancel();
            //telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }catch (Exception ex) {
        }

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

    @Override
    public void onInit(int code) {
        if (code == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.ENGLISH);


        } else {
            tts = null;
            Toast.makeText(this, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private ArrayList<Contact> prepareContactList()
    {
        ArrayList<Contact> tempList = new ArrayList<>();
        String contactNumbers = "";

        for(int i = 0; i < 2; i++)
        {

            for (Contact cont : arrayContacts)
            {
                if ((cont.canCall() && cont.getType() == 0) || (cont.getSchedule().length() == 0 && cont.getType() == 0))
                {
                    tempList.add(cont);
                }
            }
        }

        for (Contact cont : arrayContacts)
        {
            if (cont.getType() == 0 && cont.getCallOutside() == 1 && !cont.getFullName().contains("Fallback"))
            {
                tempList.add(cont);
            }
        }

        for (Contact cont : arrayContacts)
        {
            if (cont.getFullName().contains("Fallback"))
            {
                tempList.add(cont);
            }
        }

        for (Contact cont:tempList) {
            //Log.d("CALL_LIST", cont.getContactDetails() + " type " + cont.getType()  + " schedule: " + cont.getSchedule());
            Log.d("CALL_LIST", cont.getContactDetails() + " " + cont.getMobile());
        }

        return tempList;

    }

    private void callList()
    {
        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        String numbers = prefs.getString("sparksoft.smartguard.SOSnumbers", "");
        if(numbers.length() > 0)
        {
            String[] str = numbers.split(",");
            Log.d("CALL_LIST", numbers);
            Log.d("CALL_LIST", str[0]);
            String tempNumbers = "";
            for(int i = 1; i < str.length; i++)
            {
                tempNumbers += str[i] + ",";
            }
            prefs.edit().putString("sparksoft.smartguard.SOSnumbers", tempNumbers).apply();

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str[0]));
            startActivity(intent);
            Intent prevIntent = new Intent(getApplicationContext() , SOSMessageActivity.class);
            prevIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        }
    }



    private void call()//(final ArrayList<Contact> list)
    {
        try{
            sp.talk("Calling " + peopleToCall.get(0).getFullName(), true);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + peopleToCall.get(0).getMobile()));
            startActivity(intent);
            peopleToCall.remove(0);
        }
        catch (Exception ex)
        {
            callTimer.purge();
            callTimer.cancel();
            isOk = true;

        }



        /*
        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);
        prefs.edit().putInt("sparksoft.smartguard.prevStatus", -1).apply();

        sp.talk("Calling " + list.get(0).getFullName(), true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(0).getMobile()));
            startActivity(intent);

            list.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Create a new PhoneStateListener
        PhoneStateListener listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                String stateString = "N/A";
                int prev = -1;
                SharedPreferences prefs = getSharedPreferences(
                        "sparksoft.smartguard", Context.MODE_PRIVATE);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        //IDLE - 0
                        Log.d("CALL_LIST", "Idle");
                        prev = prefs.getInt("sparksoft.smartguard.prevStatus", -1);
                        if(prev == 0)
                        {


                        }
                        else if(prev == 1)
                        {


                            Log.d("CALL_LIST", "UNANSWERED");

                            try {
                                Thread.sleep(30000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
                            call(list);



                        }
                        prefs.edit().putInt("sparksoft.smartguard.prevStatus", 0).apply();


                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d("CALL_LIST", "Ofhook");
                        prev = prefs.getInt("sparksoft.smartguard.prevStatus", -1);
                        if(prev == 1)
                        {




                        }

                        prefs.edit().putInt("sparksoft.smartguard.prevStatus", 1).apply();

                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d("CALL_LIST", "Ringing");
                        break;
                    default:
                        Log.d("CALL LIST", "DEFAULT");
                        break;
                }

            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        /*
        for (Contact cont:list)
        {

            int didAnswer = prefs.getInt("sparksoft.smartguard.sosDidAnswer", 0);
            if(didAnswer == 1)
            {
                prefs.edit().putInt("sparksoft.smartguard.sosDidAnswer", 0).apply();
                prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();
                break;
            }
            sp.talk("Calling " + cont.getFullName(), true);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cont.getMobile()));
            startActivity(intent);

            try {
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 1).apply();
        */

    }

    private void caller()
    {
        SharedPreferences prefs = getSharedPreferences(
                "sparksoft.smartguard", Context.MODE_PRIVATE);

        for (Contact cont:arrayContacts)
        {
            //int status = prefs.getInt("sparksoft.smartguard.sosCallStatus", 0);
            //int didAnswer = prefs.getInt("sparksoft.smartguard.sosDidAnswer", 0);


            if(cont.getType() == 0 && cont.canCall())// && status == 0 && didAnswer == 0)
            {


                    prefs.edit().putInt("sparksoft.smartguard.callerId", cont.getId()).apply();
                    sp.talk("Calling " + cont.getFullName(), true);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cont.getMobile()));
                    startActivity(intent);

                    //finish();
                    //prefs.edit().putInt("sparksoft.smartguard.sosCallStatus", 1).apply();
                    //status = 1;
                    /*
                    while(status == 1)
                    {
                        try {
                            Thread.sleep(20000);
                            status = prefs.getInt("sparksoft.smartguard.sosCallStatus", 0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }*/
                    //status = prefs.getInt("sparksoft.smartguard.sosCallStatus", 0);
                    //didAnswer = prefs.getInt("sparksoft.smartguard.sosDidAnswer", 0);


            }
            /*
            if (didAnswer == 1)
            {
                prefs.edit().putInt("sparksoft.smartguard.sosDidAnswer", 0).apply();
                break;
            }
            */

            //Toast.makeText(getApplicationContext(), "Calling " + arrayContacts.get(0).getFullName(), Toast.LENGTH_SHORT).show();

            //CallListener caller = new CallListener(getApplicationContext(), sp, arrayContacts);


        }

    }

    private void promptSpeechInput() {

        for(int i = 0; i < 3; i++)
        {
            if(!isOk)
            {
                //Toast.makeText(getApplicationContext(), "Are you ok?", Toast.LENGTH_LONG).show();
                sp.talk("Are you ok?", true);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Say something");
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Your device does not support speech to text.",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "Do you need an emergency call?", Toast.LENGTH_SHORT).show();
                if(i==2)
                {
                    myTimer.purge();
                    myTimer.cancel();
                    isOk = true;
                    SharedPreferences prefs = getSharedPreferences(
                            "sparksoft.smartguard", Context.MODE_PRIVATE);
                    prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                    sp.talk("Emergency protocol is initiated. Smart guard will now call your contacts", true);
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mCamera.takePicture(null, null, mPicture);
                    //call(prepareContactList());




                }
            }

        }

    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_SHORT).show();
                    //txtSpeechInput.setText(result.get(0));
                    if(result.get(0).toLowerCase().equals("yes") || result.get(0).toLowerCase().equals("ok"))
                    {
                        SharedPreferences prefs = getSharedPreferences(
                                "sparksoft.smartguard", Context.MODE_PRIVATE);
                        prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                        myTimer.purge();
                        myTimer.cancel();
                        isOk = true;
                        finish();
                    }

                    else if(result.get(0).toLowerCase().equals("no"))
                    {
                        myTimer.purge();
                        myTimer.cancel();
                        isOk = true;
                        SharedPreferences prefs = getSharedPreferences(
                                "sparksoft.smartguard", Context.MODE_PRIVATE);
                        prefs.edit().putInt("sparksoft.smartguard.SOSstatus", 0).apply();
                        sp.talk("Emergency protocol is initiated. Smart guard will now call your contacts", true);
                        try {
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mCamera.takePicture(null, null, mPicture);


                        //call(prepareContactList());


                    }
                }
                break;
            }

        }
    }

}
