package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.LoginResponse;
import com.unipi.p17134.medicalcard.Singletons.User;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterFormActivity extends BaseClass {
    private EditText amka, email, password, passwordConf, fullname, dateOfBirth;
    private boolean simpleRegister, passwordHidden, passwordHiddenConf;
    private ImageButton hidePassword, hidePasswordConf;

    private Calendar min, max;
    private final SimpleDateFormat formatter = new SimpleDateFormat(UserDAO.USER_DATE_OF_BIRTH_FORMAT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        max = Calendar.getInstance();
        min = (Calendar) max.clone();
        min.add(Calendar.YEAR, -150);

        amka = findViewById(R.id.amkaRegisterInput);
        email = findViewById(R.id.emailRegisterInput);
        password = findViewById(R.id.passwordRegisterInput);
        hidePassword = findViewById(R.id.register_hide_password);
        passwordConf = findViewById(R.id.passwordConfirmationRegisterInput);
        hidePasswordConf = findViewById(R.id.register_hide_password_conf);
        fullname = findViewById(R.id.fullnameRegisterInput);
        dateOfBirth = findViewById(R.id.dateOfBirthRegisterInput);
        dateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCalendar();
                }
            }
        });
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

        simpleRegister = getIntent().getBooleanExtra("simpleRegister", true);

        passwordHidden = false;
        passwordHiddenConf = false;
        hidePassword(null);
        hidePasswordConf(null);
    }

    public void hidePassword(View view) {
        if (!passwordHidden) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
            hidePassword.setImageResource(R.drawable.ic_visibility_on);
            hidePassword.setAlpha(PASSWORD_ALPHA_HIDDEN);
        }
        else
        {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            hidePassword.setImageResource(R.drawable.ic_visibility_off);
            hidePassword.setAlpha(PASSWORD_ALPHA_SHOWING);
        }
        passwordHidden = !passwordHidden;
    }

    public void hidePasswordConf(View view) {
        if (!passwordHiddenConf) {
            passwordConf.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
            hidePasswordConf.setImageResource(R.drawable.ic_visibility_on);
            hidePasswordConf.setAlpha(PASSWORD_ALPHA_HIDDEN);
        }
        else
        {
            passwordConf.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            hidePasswordConf.setImageResource(R.drawable.ic_visibility_off);
            hidePasswordConf.setAlpha(PASSWORD_ALPHA_SHOWING);
        }
        passwordHiddenConf = !passwordHiddenConf;
    }

    private void showCalendar() {
        Calendar currentDay = Calendar.getInstance();
        try {
            Date date = formatter.parse(dateOfBirth.getText().toString());
            currentDay.setTime(date);
        }
        catch (ParseException ignored) {}

        DatePickerDialog dialog = DatePickerDialog.newInstance(
                new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        setDateOfBirth(c);
                    }
                },
                currentDay.get(Calendar.YEAR), // Initial year selection
                currentDay.get(Calendar.MONTH), // Initial month selection
                currentDay.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dialog.setMinDate(min);
        dialog.setMaxDate(max);
        dialog.showYearPickerFirst(true);
        dialog.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private void setDateOfBirth(Calendar date) {
        dateOfBirth.setText(formatter.format(date.getTime()));
    }

    public void register(View view) {
        loadingDialog.startLoadingDialog();

        Activity activity = this;

        Date date;
        try {
            date = formatter.parse(dateOfBirth.getText().toString());
        }
        catch (ParseException e) {
            loadingDialog.dismissLoadingDialog();
            Toast.makeText(this, R.string.register_form_error_need_birth, Toast.LENGTH_LONG).show();
            return;
        }

        UserDAO.register(this, new User(
                amka.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                passwordConf.getText().toString(),
                fullname.getText().toString(),
                formatter.format(date)
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