package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button registerBtn;
    boolean fromRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = findViewById(R.id.emailLoginInput);
        password = findViewById(R.id.passwordLoginInput);

        registerBtn = findViewById(R.id.registerButton);
        registerBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        fromRegister = getIntent().getBooleanExtra("fromRegister", false);
        if (fromRegister)
            registerBtn.setVisibility(View.INVISIBLE);
    }

    public void login(View view) {
        Activity activity = this;

        UserDAO.login(this, new User(username.getText().toString(), password.getText().toString()), new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                MyPrefs.setLogin(getApplicationContext(), (LoginResponse)object);

                if (fromRegister) {
                    startActivity(new Intent(activity, DoctorRegisterFormActivity.class));
                    finish();
                }
                else {
                    //Toast.makeText(activity, response.getString("message"), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(activity, MainActivity.class));
                    finish();
                }
            }

            @Override
            public <T> void onErrorResponse(T error) {

            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterPickActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (fromRegister) {
            startActivity(new Intent(this, DoctorRegisterPickActivity.class));
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
}