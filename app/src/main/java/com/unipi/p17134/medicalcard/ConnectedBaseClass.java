package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.unipi.p17134.medicalcard.custom.API;
import com.unipi.p17134.medicalcard.custom.MyPrefs;

public class ConnectedBaseClass extends AppCompatActivity {
    // Force user to login before accessing any page
    @Override
    protected void onStart() {
        super.onStart();
        if (API.UserDAO.missingToken(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
