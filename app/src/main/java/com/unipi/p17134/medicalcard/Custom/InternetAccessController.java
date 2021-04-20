package com.unipi.p17134.medicalcard.Custom;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class InternetAccessController {
    public static boolean noInternetAccess(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, MyPermissions.NETWORK_STATE_ACCESS_REQUEST);
                return false;
            }
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return false;
        else
            return true;
    }
}
