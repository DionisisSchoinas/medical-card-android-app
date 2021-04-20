package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.singletons.User;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button registerBtn;
    boolean fromRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.emailLoginInput);
        password = findViewById(R.id.passwordLoginInput);

        registerBtn = findViewById(R.id.registerButton);
        registerBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fromRegister = getIntent().getBooleanExtra("fromRegister", false);
        if (fromRegister)
            registerBtn.setVisibility(View.INVISIBLE);
    }

    public void login(View view) {
        UserDAO.login(this, new User(username.getText().toString(), password.getText().toString()), fromRegister);
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterPickActivity.class));
        finish();
    }
}