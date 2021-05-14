package com.unipi.p17134.medicalcard.Custom.Popup;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.unipi.p17134.medicalcard.Listeners.InputPopupListener;
import com.unipi.p17134.medicalcard.R;

public class InputPopup {
    public static AlertDialog showPopup(Activity activity, int title, int existingText, int positive, int negative, InputPopupListener listener) {
        return showPopup(
                activity,
                activity.getResources().getString(title),
                activity.getResources().getString(existingText),
                activity.getResources().getString(positive),
                activity.getResources().getString(negative),
                listener
        );
    }

    public static AlertDialog showPopup(Activity activity, int title, String existingText, int positive, int negative, InputPopupListener listener) {
        return showPopup(
                activity,
                activity.getResources().getString(title),
                existingText,
                activity.getResources().getString(positive),
                activity.getResources().getString(negative),
                listener
        );
    }

    public static AlertDialog showPopup(Activity activity, String title, String existingText, String positive, String negative, InputPopupListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final View layout = activity.getLayoutInflater().inflate(R.layout.edittext_dialog, null);
        builder.setView(layout);
        EditText editText = layout.findViewById(R.id.filter_dialog_input);

        builder.setCancelable(true);
        builder.setTitle(title);
        if (existingText != null && !existingText.trim().isEmpty())
            editText.setText(existingText);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositive(editText.getText().toString());
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
        return dialog;
    }
}
