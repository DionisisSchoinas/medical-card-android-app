package com.unipi.p17134.medicalcard.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.LoginActivity;
import com.unipi.p17134.medicalcard.MainActivity;
import com.unipi.p17134.medicalcard.singletons.User;

import org.json.JSONException;
import org.json.JSONObject;

public class API {
    private static String url = "http://192.168.1.5:3000";

    public static class UserDAO {
        public static boolean hasToken(Context ctx) {
            return MyPrefs.getToken(ctx) != null;
        }

        public static void login(Activity activity, User user) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("email", user.getEmail());
                postData.put("password", user.getPassword());
                String loginUrl = url + "/auth/login";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginUrl, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MyPrefs.setToken(activity, response.getString("auth_token"));
                            MyPrefs.isDoctor(activity, response.getBoolean("is_doctor"));
                            Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, MainActivity.class));
                            activity.finish();
                        } catch (JSONException e) {
                            Toast.makeText(activity, "Something went very wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "Error : " + error.networkResponse, Toast.LENGTH_LONG).show();
                    }
                });
                MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
            }
            catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Something went very wrong", Toast.LENGTH_LONG).show();
            }
        }

        public static void logout(Activity activity) {
            MyPrefs.clearLogin(activity);
            activity.startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        }
    }
}
