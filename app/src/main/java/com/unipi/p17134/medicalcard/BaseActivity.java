package com.unipi.p17134.medicalcard;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.unipi.p17134.medicalcard.custom.API;

public class BaseActivity extends AppCompatActivity {
    // Force user to login before accessing any page
    @Override
    protected void onStart() {
        super.onStart();
        if (!API.UserDAO.hasToken(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
