package com.unipi.p17134.medicalcard.API;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.Custom.BitmapConversion;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Doctor;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorDAO extends BaseDAO {
    private static int totalPages = -1;
    private static int currentPage = 0;
    private static int queueItems = 0;

    private static boolean hasQuery = false;

    public static void doctors(Activity activity, int page, String specialityQuery, DAOResponseListener responseListener) {
        // Already loading appointments
        if (queueItems > 0)
            return;

        // If not the first request and the page is out of bounds return
        if (totalPages != -1 && page > totalPages)
            return;

        // If page not specifically given (go to next page)
        if (page == -1) {
            // If last read page and total pages are the same (we are at the last page)
            page = currentPage + 1;
        }

        boolean withQuery = !(specialityQuery == null || specialityQuery.length() == 0);
        if (withQuery != hasQuery) {
            page = 1;
            hasQuery = withQuery;
        }

        String doctorsUrl = url + "/doctors?page="+page;
        if (withQuery)
            doctorsUrl += "&speciality_query="+specialityQuery.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, doctorsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject meta = response.getJSONObject("meta");

                    // Get appointments array
                    JSONArray doctors = response.getJSONArray("doctors");

                    if (doctors.length() != 0)
                        currentPage = meta.getInt("current_page");
                    totalPages = meta.getInt("total_pages");

                    // Fill list with appointments
                    JSONObject object;

                    ArrayList<Doctor> doctorsList = new ArrayList<>();
                    for (int i=0; i<doctors.length(); i++) {
                        object = doctors.getJSONObject(i);
                        doctorsList.add(
                                new Doctor()
                                        .setId(object.getInt("id"))
                                        .setOfficeAddress(object.getString("office_address"))
                                        .setSpeciality(object.getString("speciality"))
                                        .setCost(object.getLong("cost"))
                                        .setUser(new User()
                                                .setFullname(object.getJSONObject("user").getString("fullname"))
                                )
                        );
                    }
                    responseListener.onResponse(doctorsList);
                }
                catch (JSONException e) {
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
        MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
        queueItems++;
    }

    public static void doctor(Activity activity, int id, DAOResponseListener responseListener) {
        String doctorUrl = url + "/doctors/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, doctorUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Get appointment
                    JSONObject object = response.getJSONObject("doctor");
                    // Parse appointment data
                    Doctor doctor = new Doctor(
                            object.getString("speciality"),
                            object.getString("office_address"),
                            object.getString("phone"),
                            object.getString("email"),
                            object.getLong("cost"),
                            BitmapConversion.base64ToBitmap(object.getJSONObject("image").getString("image_base64"))
                    )
                            .setId(object.getInt("id"))
                            .setUser(new User()
                                    .setFullname(object.getJSONObject("user").getString("fullname"))
                            );
                    // Send appointment back to caller
                    responseListener.onResponse(doctor);
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
