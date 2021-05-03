package com.unipi.p17134.medicalcard.Singletons;

import com.unipi.p17134.medicalcard.Custom.BitmapConversion;

import org.json.JSONException;
import org.json.JSONObject;

public class QR {
    private String token;
    private int expiresAfterSeconds;

    public QR(String token) {
        this.token = token;
        this.expiresAfterSeconds = 0;
    }

    public QR(String token, int expiresAfterSeconds) {
        this.token = token;
        this.expiresAfterSeconds = expiresAfterSeconds;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            if (token != null)
                json.put("token", token);
            return json;
        }
        catch (JSONException e) {
            return null;
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiresAfterSeconds() {
        return expiresAfterSeconds;
    }

    public void setExpiresAfterSeconds(int expiresAfterSeconds) {
        this.expiresAfterSeconds = expiresAfterSeconds;
    }
}
