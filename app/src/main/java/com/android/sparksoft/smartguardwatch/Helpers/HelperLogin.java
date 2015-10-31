package com.android.sparksoft.smartguardwatch.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Database.DataSourceContacts;
import com.android.sparksoft.smartguardwatch.Database.DataSourcePlaces;
import com.android.sparksoft.smartguardwatch.Features.SpeechBot;

import com.android.sparksoft.smartguardwatch.MenuActivity;
import com.android.sparksoft.smartguardwatch.Models.Contact;
import com.android.sparksoft.smartguardwatch.Models.Place;
import com.android.sparksoft.smartguardwatch.Services.SmartGuardService;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                                    Intent myIntent = new Intent(context, MenuActivity.class);
                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(myIntent);

                                    //result = true;

                                }
                            }
                            contacts = response.getJSONArray("contacts");

                            for(int i=0; i < contacts.length(); i++)
                            {
                                //Toast.makeText(context, contacts.getJSONObject(i).get("Mobile").toString(), Toast.LENGTH_LONG).show();
                                Contact tempContact = new Contact(contacts.getJSONObject(i).getInt("ContactId"),
                                        contacts.getJSONObject(i).getString("FirstName"),
                                            contacts.getJSONObject(i).getString("LastName"),
                                                contacts.getJSONObject(i).getString("Email"),
                                                        contacts.getJSONObject(i).getString("Mobile"),
                                                                contacts.getJSONObject(i).getString("Relationship"),
                                                                        contacts.getJSONObject(i).getInt("Rank"));
                                //contactsArray.add(tempContact);
                                try
                                {
                                    dsContacts.deleteContact(contacts.getJSONObject(i).getInt("ContactId"));
                                }
                                catch (Exception ex)
                                {

                                }
                                dsContacts.createContact(tempContact);

                            }
                            memories = response.getJSONArray("memories");
                            for(int i=0; i < memories.length(); i++)
                            {
                                //Toast.makeText(context, memories.getJSONObject(i).get("MemoryName").toString(), Toast.LENGTH_LONG).show();
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

                            Vibrator v = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);
                            SharedPreferences prefs = context.getSharedPreferences(
                                    "sparksoft.smartguard", Context.MODE_PRIVATE);
                            prefs.edit().putInt("sparksoft.smartguard.status", 1).apply();
                            prefs.edit().putString("sparksoft.smartguard.auth", basicAuth).apply();
                            sp.talk("Hello " + fullname, false);
                            Toast.makeText(context, "Hello " + fullname, Toast.LENGTH_LONG).show();
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

                req.setRetryPolicy(new DefaultRetryPolicy(60000,
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


                                    //result = true;

                                }
                            }
                            contacts = response.getJSONArray("contacts");

                            for(int i=0; i < contacts.length(); i++)
                            {
                                //Toast.makeText(context, contacts.getJSONObject(i).get("Mobile").toString(), Toast.LENGTH_LONG).show();
                                Contact tempContact = new Contact(contacts.getJSONObject(i).getInt("ContactId"),
                                        contacts.getJSONObject(i).getString("FirstName"),
                                        contacts.getJSONObject(i).getString("LastName"),
                                        contacts.getJSONObject(i).getString("Email"),
                                        contacts.getJSONObject(i).getString("Mobile"),
                                        contacts.getJSONObject(i).getString("Relationship"),
                                        contacts.getJSONObject(i).getInt("Rank"));
                                //contactsArray.add(tempContact);
                                try
                                {
                                    dsContacts.deleteContact(contacts.getJSONObject(i).getInt("ContactId"));
                                }
                                catch (Exception ex)
                                {

                                }
                                dsContacts.createContact(tempContact);

                            }
                            memories = response.getJSONArray("memories");
                            for(int i=0; i < memories.length(); i++)
                            {
                                //Toast.makeText(context, memories.getJSONObject(i).get("MemoryName").toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, "Error data synching. Please try again.", Toast.LENGTH_SHORT).show();
                        sp.talk("Error data synching. Please try again.", false);
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

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(req);
    }
}
