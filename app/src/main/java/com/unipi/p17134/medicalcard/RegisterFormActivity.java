package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Singletons.User;

public class RegisterFormActivity extends AppCompatActivity {
    EditText amka, email, password, passwordConf, fullname, dateOfBirth;
    boolean simpleRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amka = findViewById(R.id.specialityDoctorRegisterInput);
        email = findViewById(R.id.officeDoctorRegisterInput);
        password = findViewById(R.id.phoneDoctorRegisterInput);
        passwordConf = findViewById(R.id.emailDoctorRegisterInput);
        fullname = findViewById(R.id.costDoctorRegisterInput);
        dateOfBirth = findViewById(R.id.dateOfBirthRegisterInput);

        simpleRegister = getIntent().getBooleanExtra("simpleRegister", true);
    }

    public void register(View view) {
        UserDAO.register(this, new User(
                amka.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                passwordConf.getText().toString(),
                fullname.getText().toString(),
                dateOfBirth.getText().toString()
        ), simpleRegister);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        backButton();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backButton();
    }

    private void backButton() {
        if (simpleRegister)
            startActivity(new Intent(this, RegisterPickActivity.class));
        else
            startActivity(new Intent(this, DoctorRegisterPickActivity.class));
        finish();
    }
}