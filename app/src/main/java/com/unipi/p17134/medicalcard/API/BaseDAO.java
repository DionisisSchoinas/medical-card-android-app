package com.unipi.p17134.medicalcard.API;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.unipi.p17134.medicalcard.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class BaseDAO {
    protected static final String url = "http://192.168.1.4:3000";
    protected static final String APPOINTMENT_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    protected static final String USER_DATE_OF_BIRTH_FORMAT = "yyyy-MM-dd";

    protected static void errorResponse(Context ctx, VolleyError error) {
        if (error.networkResponse == null) {
            Toast.makeText(ctx, ctx.getResources().getString(R.string.failed_to_speak_to_server), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = error.networkResponse.statusCode + "\n" + data.getString("message");
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(ctx, ctx.getResources().getString(R.string.problem_with_request), Toast.LENGTH_LONG).show();
        }
    }
}
