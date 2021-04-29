package com.unipi.p17134.medicalcard.Custom;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.unipi.p17134.medicalcard.R;

public class MyRequestHandler {
    private static MyRequestHandler instance;
    private RequestQueue requestQueue;

    private MyRequestHandler(Context ctx) {
        requestQueue = getRequestQueue(ctx);
    }

    public static synchronized MyRequestHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MyRequestHandler(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(Context ctx) {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> boolean addToRequestQueue(Activity activity, Request<T> req) {
        if (InternetAccessController.noInternetAccess(activity)) {
            Toast.makeText(activity, activity.getResources().getString(R.string.enable_internet_connection), Toast.LENGTH_LONG).show();
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET}, MyPermissions.INTERNET_ACCESS_REQUEST);
                return false;
            }
        }

        getRequestQueue(activity).add(req);
        return true;
    }
}
