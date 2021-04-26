package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

public class DoctorDetailsActivity extends ConnectedBaseClass {
    private int id;
    private ImageView image;
    private TextView fullname, speciality, cost, address, phone, email;

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

        id = getIntent().getIntExtra("id", 0);
        DoctorDAO.doctor(this, id, new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                Doctor doctor = (Doctor)object;
                loadDoctorData(doctor);
            }

            @Override
            public <T> void onErrorResponse(T error) {

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
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void deletionComplete() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel_appointment_success), Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();
    }
}