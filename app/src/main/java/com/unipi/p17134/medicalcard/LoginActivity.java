package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.unipi.p17134.medicalcard.custom.API;
import com.unipi.p17134.medicalcard.singletons.User;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.emailLoginInput);
        password = findViewById(R.id.passwordLoginInput);
    }

    public void login(View view) {
        API.UserDAO.login(this, new User(username.getText().toString(), password.getText().toString()));
    }

    public void register(View view) {
        API.UserDAO.login(this, new User(username.getText().toString(), password.getText().toString()));
    }
}