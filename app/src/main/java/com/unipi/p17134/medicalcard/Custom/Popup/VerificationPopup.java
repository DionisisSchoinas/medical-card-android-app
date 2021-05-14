package com.unipi.p17134.medicalcard.Custom.Popup;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.unipi.p17134.medicalcard.Listeners.InputPopupListener;
import com.unipi.p17134.medicalcard.Listeners.VerificationPopupListener;

public class VerificationPopup {
    public static void showPopup(Activity activity, int title, int message, int positive, int negative, VerificationPopupListener listener) {
        showPopup(
                activity,
                activity.getResources().getString(title),
                activity.getResources().getString(message),
                activity.getResources().getString(positive),
                activity.getResources().getString(negative),
                listener
        );
    }

    public static void showPopup(Activity activity, int title, String message, int positive, int negative, VerificationPopupListener listener) {
        showPopup(
                activity,
                activity.getResources().getString(title),
                message,
                activity.getResources().getString(positive),
                activity.getResources().getString(negative),
                listener
        );
    }

    public static void showPopup(Activity activity, String title, String message, String positive, String negative, VerificationPopupListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositive();
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNegative();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
