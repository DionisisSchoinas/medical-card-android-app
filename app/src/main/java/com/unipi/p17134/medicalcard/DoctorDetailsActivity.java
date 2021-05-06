package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

public class DoctorDetailsActivity extends ConnectedBaseClass {
    private int id;
    private ImageView image;
    private TextView fullname, speciality, cost, address, phone, email;
    private Button scheduleButton;

    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.doctor_details_photo);
        fullname = findViewById(R.id.doctor_details_name);
        speciality = findViewById(R.id.doctor_details_speciality);
        cost = findViewById(R.id.doctor_details_cost);
        address = findViewById(R.id.doctor_details_address);
        phone = findViewById(R.id.doctor_details_phone);
        email = findViewById(R.id.doctor_details_email);
        scheduleButton = findViewById(R.id.doctor_schedule_button);
        scheduleButton.setEnabled(false);

        id = getIntent().getIntExtra("id", 0);
        DoctorDAO.doctor(this, id, new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                Doctor doctor = (Doctor)object;
                loadDoctorData(doctor);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDoctorData(Doctor doctor) {
        if (doctor.getImage() != null)
            image.setImageBitmap(doctor.getImage());

        fullname.setText(doctor.getUser().getFullname());
        speciality.setText(doctor.getSpeciality());
        cost.setText(doctor.getCost()+" €");
        address.setText(doctor.getOfficeAddress());
        phone.setText(doctor.getPhone());
        email.setText(doctor.getEmail());

        this.doctor = doctor;

        scheduleButton.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyPermissions.RESPONSE_FROM_DOCTOR_SCHEDULE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

    @Override
    protected void backButton() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    public void showSchedule(View view) {
        Intent intent = new Intent(this, DoctorAppointmentScheduleActivity.class);
        intent.putExtra("doctor", doctor);
        startActivityForResult(intent, MyPermissions.RESPONSE_FROM_DOCTOR_SCHEDULE);
    }
}