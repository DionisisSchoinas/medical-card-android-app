package com.unipi.p17134.medicalcard.API;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.QR;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QrDAO extends BaseDAO{
    public static void generate(Activity activity, DAOResponseListener responseListener) {
        String qrUrl = url + "/qr/generate/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, qrUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Get QR data
                    QR qr = new QR(
                            response.getString("token"),
                            response.getInt("expires_after_seconds")
                    );

                    // Send qr back to caller
                    responseListener.onResponse(qr);
                }
                catch (JSONException e) {
                    responseListener.onErrorResponse(e);
                    //Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //errorResponse(activity, error);
                responseListener.onErrorResponse(error);
            }
        })
        {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("AuthorizationToken", MyPrefs.getToken(activity));
                params.put("content-type", "application/json");
                return params;
            }
        };
        MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
    }
}
