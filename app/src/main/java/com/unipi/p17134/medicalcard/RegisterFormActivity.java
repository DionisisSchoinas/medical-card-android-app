package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

public class RegisterFormActivity extends BaseClass {
    EditText amka, email, password, passwordConf, fullname, dateOfBirth;
    boolean simpleRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amka = findViewById(R.id.amkaRegisterInput);
        email = findViewById(R.id.emailRegisterInput);
        password = findViewById(R.id.passwordRegisterInput);
        passwordConf = findViewById(R.id.passwordConfirmationRegisterInput);
        fullname = findViewById(R.id.fullnameRegisterInput);
        dateOfBirth = findViewById(R.id.dateOfBirthRegisterInput);

        simpleRegister = getIntent().getBooleanExtra("simpleRegister", true);
    }

    public void register(View view) {
        loadingDialog.startLoadingDialog();

        Activity activity = this;

        UserDAO.register(this, new User(
                amka.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                passwordConf.getText().toString(),
                fullname.getText().toString(),
                dateOfBirth.getText().toString()
        ), new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                LoginResponse loginResponse = (LoginResponse)object;

                MyPrefs.setToken(activity, loginResponse.getAuthToken());
                MyPrefs.isDoctor(activity, loginResponse.isDoctor());
                MyPrefs.setDoctorId(activity, loginResponse.getDoctorId());

                if (simpleRegister) {
                    startActivity(new Intent(activity, MainActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(activity, DoctorRegisterFormActivity.class));
                    finish();
                }
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorMessage(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void backButton() {
        if (simpleRegister)
            startActivity(new Intent(this, RegisterPickActivity.class));
        else
            startActivity(new Intent(this, DoctorRegisterPickActivity.class));
        finish();
    }
}