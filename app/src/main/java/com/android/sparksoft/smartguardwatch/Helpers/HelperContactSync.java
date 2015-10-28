package com.android.sparksoft.smartguardwatch.Helpers;

import android.content.Context;
import android.widget.Toast;

import com.android.sparksoft.smartguardwatch.Features.SpeechBot;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 10/7/2015.
 */
public class HelperContactSync {
    private String basicAuth;
    private SpeechBot sp;
    private Context context;

    public HelperContactSync(Context _context, String _basicAuth, SpeechBot _sp)
    {
        basicAuth = _basicAuth;
        context = _context;
        sp = _sp;
    }

    public void contactSync(final String url)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONArray params = new JSONArray();

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray response)
                    {
                        //Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                //Toast.makeText(context, obj.getString("Mobile"), Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, obj.getString("FirstName") + obj.getString("LastName") +
                                        " Mobile:" + obj.getString("Mobile") + "Rank:" + obj.getString("Rank"),
                                        Toast.LENGTH_SHORT).show();


                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        sp.talk("Contacts syncing complete", false);
                    }
                },
                new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

                        if(error.networkResponse!= null) {

                            //for (String str : error.networkResponse.headers.keySet()) {
                            Toast.makeText(context, "Response Code: " +
                                    //error.networkResponse.headers.get("X-Android-Response-Source") + " " +
                                    error.networkResponse.statusCode, Toast.LENGTH_LONG).show();
                            if(error.networkResponse.statusCode == 401) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show();
                                sp.talk("Wrong username or password. Please try again.", false);
                            }
                            //}
                        }
                        else
                        {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                })
        {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", basicAuth);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjReq);
    }

}
