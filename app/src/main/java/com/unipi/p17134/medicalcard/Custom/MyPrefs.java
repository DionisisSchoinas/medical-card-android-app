package com.unipi.p17134.medicalcard.Custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.LoginActivity;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

public class MyPrefs {
    public static void logout(Activity activity) {
        MyPrefs.clearLogin(activity);
        Toast.makeText(activity, activity.getResources().getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static void clearLogin(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.remove("isDoctor");
        editor.remove("userFullname");
        editor.remove("userBirth");
        editor.apply();
    }

    public static void setLogin(Context ctx, LoginResponse loginData) {
        MyPrefs.setToken(ctx, loginData.getAuthToken());
        MyPrefs.isDoctor(ctx, loginData.isDoctor());
        MyPrefs.setUserData(ctx, loginData.getUser());
    }

    public static void setToken(Context ctx, String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getToken(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString("token", null);
    }

    public static void isDoctor(Context ctx, boolean isDoctor) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isDoctor", isDoctor);
        editor.apply();
    }

    public static boolean isDoctor(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("isDoctor", false);
    }

    public static void setUserData(Context ctx, User user) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userFullname", user.getFullname());
        editor.putString("userBirth", user.getDateOfBirth());
        editor.apply();
    }

    public static User getUserData(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        User user = new User();
        user.setFullname(preferences.getString("userFullname", ""));
        user.setDateOfBirth(preferences.getString("userBirth", ""));
        return user;
    }
}
