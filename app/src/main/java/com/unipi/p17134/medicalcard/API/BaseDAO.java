package com.unipi.p17134.medicalcard.API;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.unipi.p17134.medicalcard.R;

import org.json.JSONObject;

public class BaseDAO {
    //protected static final String url = "http://192.168.1.4:3000";
    protected static final String url = "https://medical-card-api.herokuapp.com";
    public static final String APPOINTMENT_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String USER_DATE_OF_BIRTH_FORMAT = "yyyy-MM-dd";
}
