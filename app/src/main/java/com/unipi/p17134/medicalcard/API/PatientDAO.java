package com.unipi.p17134.medicalcard.API;

import android.app.Activity;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.BitmapConversion;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Doctor;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientDAO extends BaseDAO {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(APPOINTMENT_TIME_FORMAT);
    private static int currentPage = 0;
    private static int queueItems = 0;

    public static void resetCounters() {
        currentPage = 0;
        queueItems = 0;
    }

    public static void appointments(Activity activity, int page, DAOResponseListener responseListener) {
        // Already loading appointments
        if (queueItems > 0)
            return;

        // If page not specifically given (go to next page)
        if (page == -1) {
            // If last read page and total pages are the same (we are at the last page)
            page = currentPage + 1;
        }

        String appointmentsUrl = url + "/appointments?page="+page;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, appointmentsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject meta = response.getJSONObject("meta");

                    // Get appointments array
                    JSONArray appointments = response.getJSONArray("appointments");

                    if (appointments.length() != 0)
                        currentPage = meta.getInt("current_page");

                    // Fill list with appointments
                    JSONObject object;

                    ArrayList<Appointment> appointmentsList = new ArrayList<>();
                    for (int i=0; i<appointments.length(); i++) {
                        object = appointments.getJSONObject(i);
                        appointmentsList.add(
                                new Appointment(
                                        object.getInt("id"),
                                        new Doctor()
                                                .setId(object.getJSONObject("doctor").getInt("id"))
                                                .setOfficeAddress(object.getJSONObject("doctor").getString("office_address"))
                                                .setSpeciality(object.getJSONObject("doctor").getString("speciality"))
                                                .setUser(new User()
                                                        .setFullname(object.getJSONObject("doctor").getJSONObject("user").getString("fullname"))
                                                )
                                        ,
                                        null,
                                        formatter.parse(object.getString("appointment_date_time_start")),
                                        formatter.parse(object.getString("appointment_date_time_end"))
                                )
                        );
                    }
                    responseListener.onResponse(appointmentsList);
                }
                catch (JSONException | ParseException e) {
                    //Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                    responseListener.onErrorResponse(e);
                }
                queueItems--;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                queueItems--;
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

        // Item successfully added to queue
        if (MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest))
            queueItems++;
    }

    public static void appointment(Activity activity, int id, DAOResponseListener responseListener) {
        String appointmentUrl = url + "/appointments/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, appointmentUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Get appointment
                    JSONObject object = response.getJSONObject("appointment");
                    // Parse appointment data
                    Appointment appointment = new Appointment(
                            object.getInt("id"),
                            new Doctor(
                                    object.getJSONObject("doctor").getString("speciality"),
                                    object.getJSONObject("doctor").getString("office_address"),
                                    object.getJSONObject("doctor").getString("phone"),
                                    object.getJSONObject("doctor").getString("email"),
                                    object.getJSONObject("doctor").getLong("cost"),
                                    BitmapConversion.base64ToBitmap(object.getJSONObject("doctor").getJSONObject("image").getString("image_base64"))
                                )
                                    .setId(object.getJSONObject("doctor").getInt("id"))
                                    .setUser(new User()
                                            .setFullname(object.getJSONObject("doctor").getJSONObject("user").getString("fullname"))
                                    )
                            ,
                            null,
                            formatter.parse(object.getString("appointment_date_time_start")),
                            formatter.parse(object.getString("appointment_date_time_end"))
                    );
                    // Send appointment back to caller
                    responseListener.onResponse(appointment);
                }
                catch (JSONException | ParseException e) {
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

    public static void deleteAppointment(Activity activity, int id, DAOResponseListener responseListener) {
        String appointmentUrl = url + "/appointments/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, appointmentUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Send appointment back to caller
                responseListener.onResponse(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //errorResponse(activity, error);

                // Volley can't parse 204 No Content error
                if (error instanceof ParseError)
                    responseListener.onResponse(null);
                else
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
