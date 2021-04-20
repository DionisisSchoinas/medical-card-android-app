package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.singletons.User;

public class RegisterFormActivity extends AppCompatActivity {
    EditText amka, email, password, passwordConf, fullname, dateOfBirth;
    boolean simpleRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

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
}