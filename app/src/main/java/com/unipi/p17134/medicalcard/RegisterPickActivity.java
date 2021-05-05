package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

public class RegisterPickActivity extends BaseClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pick);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void backButton() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}