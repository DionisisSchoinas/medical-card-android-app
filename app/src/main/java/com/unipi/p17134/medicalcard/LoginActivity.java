package com.unipi.p17134.medicalcard;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.RemoteMessage;
import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.LoadingDialog;
import com.unipi.p17134.medicalcard.Custom.LoadingDialogEvent;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

public class LoginActivity extends BaseClass {
    private EditText username, password;
    private Button registerBtn;
    private boolean fromRegister;

    private ConstraintLayout loginLayout;

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

        loginLayout = findViewById(R.id.login_constraint_layout);
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