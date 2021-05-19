package com.unipi.p17134.medicalcard.Custom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.unipi.p17134.medicalcard.LoginActivity;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Language;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

import java.util.Locale;

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
        editor.remove("doctorId");
        editor.remove("userFullname");
        editor.remove("userBirth");
        editor.apply();
    }

    public static void setLogin(Context ctx, LoginResponse loginData) {
        MyPrefs.setToken(ctx, loginData.getAuthToken());
        MyPrefs.isDoctor(ctx, loginData.isDoctor());
        MyPrefs.setDoctorId(ctx, loginData.getDoctorId());
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

    public static void setDoctorId(Context ctx, int id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("doctorId", id);
        editor.apply();
    }

    public static int getDoctorId(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getInt("doctorId", 0);
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

    public static class LocalePrefs {
        public static void setLocale(Activity activity, Language activityLanguage) {
            Locale activityLoc = new Locale(activityLanguage.getLanguageLocale());

            Locale newLocale = new Locale(getLanguage(activity.getApplicationContext()).getLanguageLocale());

            Locale current;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                current = activity.getResources().getConfiguration().getLocales().get(0);
            }
            else {
                current = activity.getResources().getConfiguration().locale;
            }

            if (!newLocale.equals(current) || !activityLoc.equals(current))
            {
                Locale.setDefault(newLocale);
                Resources resources = activity.getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(newLocale);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    activity.getApplicationContext().createConfigurationContext(config);
                }
                else {
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                }
                activity.recreate();
            }
        }

        public static Language getLanguage(Context ctx) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            return new Language(sharedPreferences.getString("language","en"));
        }

        public static void setLanguage(Context ctx, Language language) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", language.getLanguageLocale());
            editor.apply();
        }
    }
}
