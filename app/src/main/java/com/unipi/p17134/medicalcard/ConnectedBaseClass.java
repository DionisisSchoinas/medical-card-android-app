package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.os.Bundle;

import com.unipi.p17134.medicalcard.API.UserDAO;

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
            startActivity(intent);
            finish();
        }
    }
}
