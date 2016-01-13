package com.android.sparksoft.smartguardwatch.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Database.DataSourcePlaces;
import com.android.sparksoft.smartguardwatch.DeviceLostActivity;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;

import com.android.sparksoft.smartguardwatch.MenuActivity;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
import com.android.sparksoft.smartguardwatch.Models.AlarmUtils;
import com.android.sparksoft.smartguardwatch.Models.Constants;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Models.Place;
import com.android.sparksoft.smartguardwatch.Services.FallService;
import com.android.sparksoft.smartguardwatch.Services.SmartGuardService;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 10/7/2015.
 */
public class HelperLogin {

    private String basicAuth;
    private SpeechBot sp;
    private Context context;
    private boolean result;
    private ArrayList<Contact> contactsArray;
    private DataSourceContacts dsContacts;
    private DataSourcePlaces dsPlaces;

    public HelperLogin(Context _context, String _basicAuth, SpeechBot _sp)
    {
        basicAuth = _basicAuth;
        context = _context;
        sp = _sp;
        result = false;
        //contactsArray = new ArrayList<Contact>();
        dsContacts = new DataSourceContacts(context);
        dsContacts.open();

        dsPlaces = new DataSourcePlaces(context);
        dsPlaces.open();

    }

    public boolean getResult()
    {
        return result;
    }

    public ArrayList<Contact> getContactsList()
    {
        return contactsArray;
    }

    public float getBatteryLevel() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    public void sendFallData(String url, byte[] image)
    {


        //Double toBeTruncated = new Double("3.5789055");

        //Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        try {
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    .format(new Date());
            String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
            params.put("FallTimeStamp", timeStamp);
            params.put("image", encodedImage);
            params.put("FallLat", prefs.getString(Constants.PREFS_GPS_LAT, null));
            params.put("FallLong", prefs.getString(Constants.PREFS_GPS_LONG, null));



        } catch (JSONException e) {
            e.printStackTrace();
        }
        //params.put("token", "AbCdEfGh123456");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("LOG_HELPER", response.toString());
                        Toast.makeText(context, "Fall data sent successfully", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("LOG_HELPER", error.toString());
                    }
                })
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", basicAuth);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(req);
    }

    public void sendTrack(String url, String lat, String lon)
    {
        /*
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        String timeStamp = Integer.toString(cal.get(Calendar.YEAR)) + "-" +
                Integer.toString(cal.get(Calendar.MONTH)+1) + "-" +
                Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "T" +
                Integer.toString(hour) + ":" + Integer.toString(min) + ":" +
                Integer.toString(sec);
                */
        Log.d("LOG_HELPER", Float.toString(getBatteryLevel()));
        //Log.d("LOG_HELPER", timeStamp);


        //Double toBeTruncated = new Double("3.5789055");

        //Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        try {
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    .format(new Date());

            params.put("timestamp", timeStamp);

            params.put("TrackLat", lat);
            params.put("TrackLong", lon);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        //params.put("token", "AbCdEfGh123456");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            //Log.d("LOG_HELPER", response.getString("value"));
                            String status = response.getString("value");
                            if(status.equals("1")) {
                                Log.d("LOG_HELPER", "device is missing");
                                SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                                prefs.edit().putString("MissingSpiel", response.getString("response")).apply();

                                int deviceStatus = prefs.getInt("deviceStatus", 0);

                                if(deviceStatus != 1)
                                {
                                    Intent lostIntent = new Intent(context, DeviceLostActivity.class);
                                    lostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(lostIntent);
                                    prefs.edit().putInt("deviceStatus", 1).apply();

                                }

                            }
                            else {
                                Log.d("LOG_HELPER", "device is not missing");
                                SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                                prefs.edit().putInt("deviceStatus", 0).apply();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(context, "Watch data sent successfully", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("LOG_HELPER", error.toString());
                    }
                })
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", basicAuth);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(req);

    }


    public void sendChargeData(String url)
    {
        /*
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        String timeStamp = Integer.toString(cal.get(Calendar.YEAR)) + "-" +
                Integer.toString(cal.get(Calendar.MONTH)+1) + "-" +
                Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "T" +
                Integer.toString(hour) + ":" + Integer.toString(min) + ":" +
                Integer.toString(sec);
                */
        Log.d("LOG_HELPER", Float.toString(getBatteryLevel()));
        //Log.d("LOG_HELPER", timeStamp);



        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        int active = prefs.getInt(Constants.ACTIVE_COUNTER, 0);
        int inactive = prefs.getInt(Constants.INACTIVE_COUNTER, 0);
        int fall = prefs.getInt(Constants.FALL_COUNTER, 0);
        //int fall = prefs.getInt(Constants.FALL_COUNTER, 0);

        double total = active + inactive;
        double activePct = active/total*100;
        double inactivePct = inactive/total*100;
        Log.d("LOG_HELPER", Double.toString(activePct));
        Log.d("LOG_HELPER", Double.toString(inactivePct));
        //Double toBeTruncated = new Double("3.5789055");

        //Double truncatedDouble=new BigDecimal(toBeTruncated ).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        try {
            params.put("ChargePct", getBatteryLevel());
            //params.put("ChargeTimeStamp", timeStamp);
            params.put("ActivePct", String.format("%.2f", activePct));
            params.put("InactivePct", String.format("%.2f",inactivePct));
            params.put("FallCount", Integer.toString(fall));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //params.put("token", "AbCdEfGh123456");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("LOG_HELPER", response.toString());
                        Toast.makeText(context, "Watch data sent successfully", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("LOG_HELPER", error.toString());
                    }
                })
                {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", basicAuth);
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }

        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(req);

    }

    public void processAlarm(String memories)
    {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        String alarmString = prefs.getString(Constants.PREFS_ALARM_STING, "");

        ArrayList<Alarm> alarms = null;
        try {
            alarms = AlarmUtils.parseAlarmString(alarmString);
            AlarmUtils.cancelAllAlarms(context, alarms);
            //AlarmUtils.startAllAlarms(getApplicationContext(), alarms);
        } catch (JSONException e) {
            e.printStackTrace();
        }





        try {
            alarms = AlarmUtils.parseAlarmString("{\n" +
                    "\"memories\":" + memories + "}");
            AlarmUtils.startAllAlarms(context, alarms);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prefs.edit().putString(Constants.PREFS_ALARM_STING, "{\n" +
                "\"memories\":" + memories + "}").apply();

    }


    public void SyncHelperJSONObject(String url)
    {
        //Toast.makeText(context, "Sending JSON request.", Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject params = new JSONObject();
        //params.put("token", "AbCdEfGh123456");
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray contacts = null, memories = null, places = null, responses = null, vitals = null;
                        JSONObject fall = null;
                        String fullname = " ", version = "";

                        try {

                            PackageInfo pInfo = null;
                            String appVersion = "";
                            try {
                                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                                appVersion = pInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            version = response.getString("version");

                            if(!version.equals(appVersion) && getBatteryLevel() > 70.0)
                            {
                                Toast.makeText(context, "Downloading updates. Please do not turn off the watch.",
                                        Toast.LENGTH_LONG).show();
                                Update("");
                            }
                            responses = response.getJSONArray("responses");
                            for(int i= 0; i < responses.length(); i++)
                            {
                                if (responses.getJSONObject(i).getString("response").equals("Name"))
                                    fullname = responses.getJSONObject(i).getString("value");
                                else if(responses.getJSONObject(i).getString("response").equals("Result")) {
                                    //Intent myIntent = new Intent(context, MenuActivity.class);
                                    //myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    //context.startActivity(myIntent);
                                    SharedPreferences prefs = context.getSharedPreferences(
                                            Constants.PREFS_NAME, Context.MODE_PRIVATE);
                                    prefs.edit().putInt(Constants.PREFS_LOG_CHECKER, 1).apply();
                                    prefs.edit().putString(Constants.PREFS_AUTH, basicAuth).apply();
                                    //result = true;

                                }
                            }
                            contacts = response.getJSONArray("contacts");
                            //Toast.makeText(context, dsContacts.getAllContacts().size(), Toast.LENGTH_LONG).show();
//                            Log.e("DSCONTACTS", Integer.toString(dsContacts.getAllContacts().size()));
                            dsContacts.deleteAllContacts();
                            for(int i=0; i < contacts.length(); i++)
                            {
                                //Toast.makeText(context, contacts.getJSONObject(i).get("schedule").toString(), Toast.LENGTH_LONG).show();
                                Contact tempContact = new Contact(contacts.getJSONObject(i).getInt("ContactId"),
                                        contacts.getJSONObject(i).getString("FirstName"),
                                        contacts.getJSONObject(i).getString("LastName"),
                                        contacts.getJSONObject(i).getString("Email"),
                                        contacts.getJSONObject(i).getString("Mobile"),
                                        contacts.getJSONObject(i).getString("Relationship"),
                                        contacts.getJSONObject(i).getInt("Rank"),
                                        contacts.getJSONObject(i).getString("schedule"),
                                        contacts.getJSONObject(i).getInt("type"),
                                        contacts.getJSONObject(i).getInt("canContactOutside"));
                                //contactsArray.add(tempContact);
                                Log.d("LOG_CONTACT", tempContact.getContactDetails());
                                dsContacts.createContact(tempContact);

                            }
                            memories = response.getJSONArray("memories");

                            processAlarm(memories.toString());

                            vitals = response.getJSONArray("vitals");
                            String vitalString = "";
                            for(int i=0; i< vitals.length(); i++) {
                                    vitalString += vitals.getJSONObject(i).getString("Title") + ": " +
                                            vitals.getJSONObject(i).getString("Value") + ";";

                            }
                            SharedPreferences vitalprefs = context.getSharedPreferences(
                                    Constants.PREFS_NAME, Context.MODE_PRIVATE);
                            vitalprefs.edit().putString("sparksoft.smartguard.vitals", vitalString).apply();


                            //Toast.makeText(context, memories.toString(), Toast.LENGTH_LONG).show();
                            for(int i=0; i < memories.length(); i++)
                            {
                                //Toast.makeText(context, memories.getJSONObject(i).get("MemoryName").toString(), Toast.LENGTH_LONG).show();
                            }

                            fall = response.getJSONObject("fallProfile");
                            {
                                SharedPreferences prefs = context.getSharedPreferences(
                                        Constants.PREFS_NAME, Context.MODE_PRIVATE);
                                Log.d("FALL_PROFILE", "FALL UPPER THRESHOLD: " + Double.toString(fall.getDouble("fallUpperThreshold")));

                                prefs.edit().putString(Constants.PREFS_FALL_PARAMETER_FUT, Double.toString(fall.getDouble("fallUpperThreshold"))).apply();


                                Log.d("FALL_PROFILE", "FALL WINDOW DURATION: " + fall.getString("fallWindowDuration"));
                                prefs.edit().putString(Constants.PREFS_FALL_PARAMETER_FWD, fall.getString("fallWindowDuration")).apply();

                                Log.d("FALL_PROFILE", "PEAK UPPER THRESHOLD: " + Integer.toString(fall.getInt("fallPeakUpperThreshold")));
                                prefs.edit().putInt(Constants.PREFS_FALL_PARAMETER_PUT, fall.getInt("fallPeakUpperThreshold")).apply();

                                Log.d("FALL_PROFILE", "PEAK LOWER THRESHOLD: " + Integer.toString(fall.getInt("fallPeakLowerThreshold")));
                                prefs.edit().putInt(Constants.PREFS_FALL_PARAMETER_PLT, fall.getInt("fallPeakLowerThreshold")).apply();

                                Log.d("FALL_PROFILE", "RESIDUAL MOVEMENT THRESHOLD: " + Double.toString(fall.getDouble("residualMovementThreshold")));
                                prefs.edit().putString(Constants.PREFS_FALL_PARAMETER_RMT, Double.toString(fall.getDouble("residualMovementThreshold"))).apply();


                                Log.d("FALL_PROFILE", "RESIDUAL MOVEMENT DURATION: " + fall.getString("residualWindowDuration"));
                                prefs.edit().putString(Constants.PREFS_FALL_PARAMETER_RMD, fall.getString("residualWindowDuration")).apply();


                                Log.d("FALL_PROFILE", "ACTIVE: " + fall.getBoolean("isActive"));
                                Log.d("FALL_PROFILE", fall.getString("description"));

                                Log.d("FALL_PROFILE", "Inactivity duration: " + fall.getInt("inactivityDuration"));
                                prefs.edit().putInt(Constants.PREFS_INACTIVITY_DURATION, fall.getInt("inactivityDuration")).apply();

                                Log.d("FALL_PROFILE", "Fitminute duration: " + fall.getInt("fitminuteDuration"));
                                prefs.edit().putInt(Constants.PREFS_FITMINUTE_DURATION, fall.getInt("fitminuteDuration")).apply();

                                Log.d("FALL_PROFILE", "Active threshold: " + fall.getDouble("activeThreshold"));
                                prefs.edit().putString(Constants.PREFS_ACTIVITY_THRESHOLD, Double.toString(fall.getDouble("activeThreshold"))).apply();

                                Log.d("FALL_PROFILE", "Fitminute threshold: " + fall.getDouble("fitminuteThreshold"));
                                prefs.edit().putString(Constants.PREFS_FITMINUTE_THRESHOLD, Double.toString(fall.getDouble("fitminuteThreshold"))).apply();
                            }

                            places = response.getJSONArray("places");
                            //dsPlaces.deleteAllPlaces();

                            ArrayList<Place> allPlaces = dsPlaces.getAllPlaces();
                            for(int i = 0; i < allPlaces.size(); i++)
                            {

                                //Log.e("DSCONTACTS", "DELETED ID: " + Integer.toString(allContacts.get(i).getId()));
                                dsPlaces.deletePlace(allPlaces.get(i).getId());


                            }
                            for(int i=0; i< places.length(); i++)
                            {
                                Place tempPlace = new Place(places.getJSONObject(i).getInt("PlaceId"),
                                        places.getJSONObject(i).getString("PlaceName"),
                                        places.getJSONObject(i).getString("PlaceLat"),
                                        places.getJSONObject(i).getString("PlaceLong"));
                                //Toast.makeText(context, tempPlace.getId() + " " +
                                //    tempPlace.getPlaceName(), Toast.LENGTH_SHORT).show();
                                if(tempPlace.getPlaceName().contains("Home"))
                                {

                                    SharedPreferences prefs = context.getSharedPreferences(
                                            Constants.PREFS_NAME, Context.MODE_PRIVATE);
                                    prefs.edit().putString(Constants.HOME_LATITUDE, tempPlace.getPlaceLat()).apply();
                                    prefs.edit().putString(Constants.HOME_LONGITUDE, tempPlace.getPlaceLong()).apply();
                                    Log.d("Navigation", "Home Lat:" + tempPlace.getPlaceLat());
                                    Log.d("Navigation", "Home Long:" + tempPlace.getPlaceLong());
                                }
                                dsPlaces.createPlace(tempPlace);
                                //Toast.makeText(context, places.getJSONObject(i).get("PlaceName").toString(), Toast.LENGTH_LONG).show();
                            }

                            Vibrator v = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);


                            SharedPreferences prefs = context.getSharedPreferences(
                                    Constants.PREFS_NAME, Context.MODE_PRIVATE);

                            prefs.edit().putInt("sparksoft.smartguard.status", 1).apply();
                            //prefs.edit().putInt("sparksoft.smartguard.loginStatus", 1).apply();
                            //prefs.edit().putString("sparksoft.smartguard.auth", basicAuth).apply();
                            //sp.talk("Hello " + fullname, false);
                            //Intent fallIntent = new Intent(context, FallService.class);
                            //startService(new Intent(getApplicationContext(), FallService.class));
                            //context.stopService(fallIntent);
                            //context.startService(fallIntent);
                            //Toast.makeText(context, "Hello " + fullname, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(context, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
                        sp.talk("Error logging in. Please try again.", false);
                    }
                })
                {

                /**
                 * Passing some request headers
                 * */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", basicAuth);
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                };

                req.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(req);
    }

    public void Sync(String url)
    {
        SyncHelperJSONObject(url);
    }

    public void Update(final String apkurl){

            new Thread(){
                @Override
                public void run() {
                    Looper.prepare();
                    super.run();

                    try{

                        URL url = new URL("http://smartguardwatch.azurewebsites.net/AppBuild/DownloadLatest");

                        HttpURLConnection c = (HttpURLConnection) url.openConnection();
                        c.setRequestMethod("GET");
                        //c.setRequestProperty("Authorization", basicAuth);
                        c.setDoOutput(true);
                        c.connect();

                        String PATH = Environment.getExternalStorageDirectory() + "/download/";
                        File file = new File(PATH);
                        file.mkdirs();
                        File outputFile = new File(file, "app.apk");
                        FileOutputStream fos = new FileOutputStream(outputFile);

                        InputStream is = c.getInputStream();

                        byte[] buffer = new byte[1024];
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
                        fos.close();
                        is.close();//till here, it works fine - .apk is download to my sdcard in download file

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (IOException e) {
                        Toast.makeText(context, "Update error!", Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
    }

    public void sendBatteryLevel()
    {

    }
}
