package com.unipi.p17134.medicalcard.API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.DoctorRegisterFormActivity;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.LoginActivity;
import com.unipi.p17134.medicalcard.MainActivity;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Singletons.Doctor;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class UserDAO extends BaseDAO {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(USER_DATE_OF_BIRTH_FORMAT);

    public static boolean missingToken(Context ctx) {
        return MyPrefs.getToken(ctx) == null;
    }

    public static void login(Activity activity, User user, DAOResponseListener responseListener) {
        JSONObject postData = user.toJson();
        if (postData == null) {
            responseListener.onErrorResponse(null);
        }
        else {
            String loginUrl = url + "/auth/login";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        LoginResponse loginResponse = new LoginResponse(
                                response.getString("auth_token"),
                                response.getBoolean("is_doctor"),
                                response.getInt("doctor_id"),
                                new User()
                                        .setFullname(response.getString("fullname"))
                                        .setDateOfBirth(DateTimeParsing.dateToDateString(formatter.parse(response.getString("date_of_birth"))
                                                )
                                        )
                        );
                        responseListener.onResponse(loginResponse);
                    }
                    catch (JSONException | ParseException e) {
                        //Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                        responseListener.onErrorResponse(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //errorResponse(activity, error);
                    responseListener.onErrorResponse(error);
                }
            });
            MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
        }
    }

    public static void register(Activity activity, User user, DAOResponseListener responseListener) {
        JSONObject postData = user.toJson();
        if (postData == null) {
            responseListener.onErrorResponse(null);
        }
        else {
            String signupUrl = url + "/signup";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signupUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        LoginResponse loginResponse = new LoginResponse(
                                response.getString("auth_token"),
                                response.getBoolean("is_doctor"),
                                response.getInt("doctor_id"),
                                null
                        );
                        responseListener.onResponse(loginResponse);
                    }
                    catch (JSONException e) {
                        //Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                        responseListener.onErrorResponse(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //errorResponse(activity, error);
                    responseListener.onErrorResponse(error);
                }
            });
            MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
        }
    }

    public static void registerDoctor(Activity activity, Doctor doctor, DAOResponseListener responseListener) {
        JSONObject postData = doctor.toJson();
        if (postData == null) {
            responseListener.onErrorResponse(null);
        }
        else {
            String signupUrl = url + "/doctors";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signupUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        LoginResponse loginResponse = new LoginResponse(
                                null,
                                true,
                                response.getInt("doctor_id"),
                                null
                        );
                        responseListener.onResponse(loginResponse);
                    }
                    catch (JSONException e) {
                        //Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                        responseListener.onErrorResponse(e);
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
}
