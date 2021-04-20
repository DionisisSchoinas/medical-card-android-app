package com.unipi.p17134.medicalcard;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.unipi.p17134.medicalcard.API.UserDAO;

public class ConnectedBaseClass extends AppCompatActivity {
    // Force user to login before accessing any page
    @Override
    protected void onStart() {
        super.onStart();
        if (UserDAO.missingToken(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
