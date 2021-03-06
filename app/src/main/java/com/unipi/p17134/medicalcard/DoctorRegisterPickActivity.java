package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

public class DoctorRegisterPickActivity extends BaseClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register_pick);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.doctor_register_pick_activity));
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

    @Override
    protected void backButton() {
        startActivity(new Intent(this, RegisterPickActivity.class));
        finish();
    }
}