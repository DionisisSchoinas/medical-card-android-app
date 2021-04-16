package com.unipi.p17134.medicalcard.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.collection.ArraySet;

import com.unipi.p17134.medicalcard.singletons.User;

import java.util.Set;

public class MyPrefs {
    public static void clearLogin(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.remove("isDoctor");
        editor.apply();
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
        user.setFullname(preferences.getString("userBirth", ""));
        return user;
    }
}
