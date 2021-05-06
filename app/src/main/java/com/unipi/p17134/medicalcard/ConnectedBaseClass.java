package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;

public class ConnectedBaseClass extends BaseClass {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
    }

    // Force user to login before accessing any page
    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }

    private void checkLogin() {
        if (UserDAO.missingToken(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    protected <T> boolean unauthorizedResponse(T error) {
        try {
            VolleyError volleyError = (VolleyError) error;
            if (volleyError.networkResponse.statusCode == 401) {
                MyPrefs.clearLogin(this);
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }
}
