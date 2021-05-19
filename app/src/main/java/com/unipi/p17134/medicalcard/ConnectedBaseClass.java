package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;

public class ConnectedBaseClass extends BaseClass {
    // Force user to login before accessing any page
    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    private void checkLogin() {
        if (UserDAO.missingToken(this)) {
            backToLogin();
        }
    }

    protected <T> boolean errorResponse(T error) {
        try {
            VolleyError volleyError = (VolleyError) error;

            if (volleyError.networkResponse == null) {
                Toast.makeText(this, R.string.failed_to_speak_to_server, Toast.LENGTH_SHORT).show();
                return true;
            }

            if (volleyError.networkResponse.statusCode == 401) {
                MyPrefs.clearLogin(this);
                backToLogin();
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
