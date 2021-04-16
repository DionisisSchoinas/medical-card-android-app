package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DoctorRegisterPickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register_pick);
    }

    public void needAccount(View view) {
        Intent intent = new Intent(this, RegisterFormActivity.class);
        intent.putExtra("simpleRegister", false);
        startActivity(intent);
        finish();
    }

    public void haveAccount(View view) {
        startActivity(new Intent(this, DoctorRegisterFormActivity.class));
        finish();
    }
}