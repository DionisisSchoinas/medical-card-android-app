package com.unipi.p17134.medicalcard.API;

import android.app.Activity;

import androidx.appcompat.content.res.AppCompatResources;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Patient;
import com.unipi.p17134.medicalcard.Singletons.QR;
import com.unipi.p17134.medicalcard.Singletons.QrResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QrDAO extends BaseDAO{
    private static final SimpleDateFormat formatter = new SimpleDateFormat(APPOINTMENT_TIME_FORMAT);

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

    public static void read(Activity activity, QR qr, DAOResponseListener responseListener) {
        JSONObject postData = qr.toJson();
        if (postData == null) {
            responseListener.onErrorResponse(null);
        }
        else {
            String qrUrl = url + "/qr/read/";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, qrUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        QrResponse qrData = new QrResponse();

                        JSONObject appointment = response.getJSONObject("appointment");
                        if (appointment.has("id")) {
                            qrData.setCurrentAppointment(
                                    new Appointment(
                                            appointment.getInt("id"),
                                            null,
                                            null,
                                            formatter.parse(appointment.getString("appointment_date_time_start")),
                                            formatter.parse(appointment.getString("appointment_date_time_end"))
                                    )
                            );
                        }

                        JSONObject prevAppointment = response.getJSONObject("previous_appointment");
                        if (prevAppointment.has("id")) {
                            qrData.setPreviousAppointment(
                                    new Appointment(
                                            prevAppointment.getInt("id"),
                                            null,
                                            null,
                                            formatter.parse(prevAppointment.getString("appointment_date_time_start")),
                                            null
                                    )
                            );
                        }

                        JSONObject patient = response.getJSONObject("patient");
                        if (patient.has("id")) {
                            qrData.setPatient(
                                    new Patient(
                                            patient.getInt("id"),
                                            new User()
                                                    .setFullname(patient.getString("fullname"))
                                                    .setDateOfBirth(patient.getString("date_of_birth"))
                                    )
                            );
                        }

                        // Send data back to caller
                        responseListener.onResponse(qrData);
                    } catch (JSONException | ParseException e) {
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
            }) {    //this is the part, that adds the header to the request
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
}
