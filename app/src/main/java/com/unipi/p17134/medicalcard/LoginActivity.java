package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

public class LoginActivity extends BaseClass {
    private EditText username, password;
    private Button registerBtn;
    private ImageButton hidePassword;
    private boolean fromRegister, passwordHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.login_activity));

        username = findViewById(R.id.emailLoginInput);
        password = findViewById(R.id.passwordLoginInput);
        hidePassword = findViewById(R.id.login_hide_password);
        setPasswordHidden(true);

        registerBtn = findViewById(R.id.registerButton);
        registerBtn.setVisibility(View.VISIBLE);

        ConstraintLayout loginLayout = findViewById(R.id.login_constraint_layout);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) loginLayout.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.gravity = Gravity.TOP;
        }
        else {
            params.gravity = Gravity.CENTER_VERTICAL;
        }
        loginLayout.setLayoutParams(params);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fromRegister = getIntent().getBooleanExtra("fromRegister", false);
        if (fromRegister)
            registerBtn.setVisibility(View.INVISIBLE);
    }

    public void hidePassword(View view) {
        setPasswordHidden(!passwordHidden);
    }

    private void setPasswordHidden(boolean hide) {
        if (hide) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
            hidePassword.setImageResource(R.drawable.ic_visibility_off);
            hidePassword.setAlpha(PASSWORD_ALPHA_HIDDEN);
        }
        else
        {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            hidePassword.setImageResource(R.drawable.ic_visibility_on);
            hidePassword.setAlpha(PASSWORD_ALPHA_SHOWING);
        }
        passwordHidden = hide;
    }

    public void login(View view) {
        Activity activity = this;

        loadingDialog.startLoadingDialog();

        UserDAO.login(this, new User(username.getText().toString(), password.getText().toString()), new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
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
                loadingDialog.dismissLoadingDialog();
                if (errorMessage(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
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