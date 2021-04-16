package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegisterPickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pick);
    }

    public void simpleUser(View view) {
        Intent intent = new Intent(this, RegisterFormActivity.class);
        intent.putExtra("simpleRegister", true);
        startActivity(intent);
        finish();
    }

    public void doctor(View view) {
        startActivity(new Intent(this, DoctorRegisterPickActivity.class));
        finish();
    }
}