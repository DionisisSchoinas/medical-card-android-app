package com.unipi.p17134.medicalcard.API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.DoctorRegisterFormActivity;
import com.unipi.p17134.medicalcard.LoginActivity;
import com.unipi.p17134.medicalcard.MainActivity;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.custom.MyPrefs;
import com.unipi.p17134.medicalcard.custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.singletons.Doctor;
import com.unipi.p17134.medicalcard.singletons.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserDAO extends BaseDAO {
    public static boolean missingToken(Context ctx) {
        return MyPrefs.getToken(ctx) == null;
    }

    public static void login(Activity activity, User user, boolean fromRegister) {
        JSONObject postData = user.toJson();
        if (postData == null) {
            Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
        }
        else {
            String loginUrl = url + "/auth/login";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        MyPrefs.setToken(activity, response.getString("auth_token"));
                        MyPrefs.isDoctor(activity, response.getBoolean("is_doctor"));
                        MyPrefs.setUserData(activity, new User().setFullname(response.getString("fullname")).setDateOfBirth(response.getString("date_of_birth")));

                        if (fromRegister) {
                            activity.startActivity(new Intent(activity, DoctorRegisterFormActivity.class));
                            activity.finish();
                        }
                        else {
                            Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, MainActivity.class));
                            activity.finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorResponse(activity, error);
                }
            });
            MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
        }
    }

    public static void logout(Activity activity) {
        MyPrefs.clearLogin(activity);
        Toast.makeText(activity, activity.getResources().getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static void register(Activity activity, User user, boolean simpleRegister) {
        JSONObject postData = user.toJson();
        if (postData == null) {
            Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
        }
        else {
            String signupUrl = url + "/signup";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signupUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        MyPrefs.setToken(activity, response.getString("auth_token"));
                        MyPrefs.isDoctor(activity, response.getBoolean("is_doctor"));
                        Toast.makeText(activity, response.getString("message"), Toast.LENGTH_SHORT).show();

                        if (simpleRegister) {
                            activity.startActivity(new Intent(activity, MainActivity.class));
                            activity.finish();
                        }
                        else {
                            activity.startActivity(new Intent(activity, DoctorRegisterFormActivity.class));
                            activity.finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorResponse(activity, error);
                }
            });
            MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
        }
    }

    public static void registerDoctor(Activity activity, Doctor doctor) {
        JSONObject postData = doctor.toJson();
        if (postData == null) {
            Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
        }
        else {
            String signupUrl = url + "/doctors";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signupUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        MyPrefs.isDoctor(activity, true);
                        Toast.makeText(activity, response.getString("message"), Toast.LENGTH_SHORT).show();

                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    } catch (JSONException e) {
                        Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorResponse(activity, error);
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
