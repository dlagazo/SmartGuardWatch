package com.android.sparksoft.smartguardwatch.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Database.DataSourcePlaces;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;

import com.android.sparksoft.smartguardwatch.MenuActivity;
import com.android.sparksoft.smartguardwatch.Models.Alarm;
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

import java.math.BigDecimal;
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
            alarms = Alarm.parseAlarmString(alarmString);
            Alarm.cancelAllAlarms(context, alarms);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            alarms = Alarm.parseAlarmString("{\n" +
                    "\"memories\":" + memories + "}");
            Alarm.startAllAlarms(context, alarms);
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
                        JSONArray contacts = null, memories = null, places = null, responses = null;
                        String fullname = " ";
                        try {
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




                            //Toast.makeText(context, memories.toString(), Toast.LENGTH_LONG).show();
                            for(int i=0; i < memories.length(); i++)
                            {
                                //Toast.makeText(context, memories.getJSONObject(i).get("MemoryName").toString(), Toast.LENGTH_LONG).show();
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
        //Toast.makeText(context, "Sending JSON request.", Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject params = new JSONObject();
        //params.put("token", "AbCdEfGh123456");
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray contacts = null, memories = null, places = null, responses = null;
                        String fullname = " ";
                        try {
                            responses = response.getJSONArray("responses");
                            for(int i= 0; i < responses.length(); i++)
                            {
                                if (responses.getJSONObject(i).getString("response").equals("Name"))
                                    fullname = responses.getJSONObject(i).getString("value");
                                else if(responses.getJSONObject(i).getString("response").equals("Result")) {
                                    Toast.makeText(context, "Data synching complete", Toast.LENGTH_LONG).show();

                                    //result = true;

                                }
                            }
                            contacts = response.getJSONArray("contacts");
                            dsContacts.deleteAllContacts();

                            for(int i=0; i < contacts.length(); i++)
                            {
                                //Toast.makeText(context, contacts.getJSONObject(i).get("Mobile").toString(), Toast.LENGTH_LONG).show();
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

                                dsContacts.createContact(tempContact);

                            }
                            memories = response.getJSONArray("memories");
                            processAlarm(memories.toString());

                            ArrayList<Alarm> alarms = Alarm.parseAlarmString(memories.toString());
                            for(Alarm a : alarms) {
                                Toast.makeText(context, a.toString(), Toast.LENGTH_SHORT).show();
                            }



                            for(int i=0; i < memories.length(); i++)
                            {

                                Toast.makeText(context, memories.getJSONObject(i).get("MemoryName").toString(), Toast.LENGTH_LONG).show();
                            }
                            places = response.getJSONArray("places");
                            //dsPlaces.deleteAllPlaces();
                            for(int i=0; i< places.length(); i++)
                            {
                                Place tempPlace = new Place(places.getJSONObject(i).getInt("PlaceId"),
                                        places.getJSONObject(i).getString("PlaceName"),
                                        places.getJSONObject(i).getString("PlaceLat"),
                                        places.getJSONObject(i).getString("PlaceLong"));
                                //Toast.makeText(context, tempPlace.getId() + " " +
                                //    tempPlace.getPlaceName(), Toast.LENGTH_SHORT).show();
                                try
                                {
                                    dsPlaces.deletePlace(places.getJSONObject(i).getInt("PlaceId"));
                                }
                                catch (Exception ex)
                                {

                                }
                                dsPlaces.createPlace(tempPlace);
                                //Toast.makeText(context, places.getJSONObject(i).get("PlaceName").toString(), Toast.LENGTH_LONG).show();
                            }

                            //Vibrator v = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            //v.vibrate(500);
                            //sp.talk("Data synching complete", false);
                            //SharedPreferences prefs = context.getSharedPreferences(
                            //        "sparksoft.smartguard", Context.MODE_PRIVATE);
                            //prefs.edit().putInt("sparksoft.smartguard.status", 1).apply();
                            //prefs.edit().putString("sparksoft.smartguard.auth", basicAuth).apply();
                            //sp.talk("Hello " + fullname, false);


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
                        Toast.makeText(context, "Error data synching. Please try again.", Toast.LENGTH_SHORT).show();

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

    public void sendBatteryLevel()
    {

    }
}
