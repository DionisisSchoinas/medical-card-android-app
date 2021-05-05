package com.unipi.p17134.medicalcard;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Patient;

public class QRAppointmentInfoActivity extends ConnectedBaseClass {
    private Appointment current;
    private Appointment previous;
    private Patient patient;

    private TextView name, birth, currentDate, previousDate;
    private ImageView patientMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_appointment_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.appointment_details_qr_name);
        birth = findViewById(R.id.appointment_details_qr_birth);
        currentDate = findViewById(R.id.appointment_details_qr_today_date);
        previousDate = findViewById(R.id.appointment_details_qr_previous_date);
        patientMatch = findViewById(R.id.patient_match_display);
        patientMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton();
            }
        });

        current = getIntent().getParcelableExtra("current");
        previous = getIntent().getParcelableExtra("previous");
        patient = getIntent().getParcelableExtra("patient");

        if (patient == null) {
            patientMatch.setImageDrawable(getResources().getDrawable(R.drawable.ic_error, getApplicationContext().getTheme()));
        }
        else {
            patientMatch.setImageDrawable(getResources().getDrawable(R.drawable.ic_check, getApplicationContext().getTheme()));
            name.setText(patient.getUser().getFullname());
            birth.setText(patient.getUser().getDateOfBirth());
            if (current != null) {
                String s = DateTimeParsing.dateToDateString(current.getStartDate()) + "  " + DateTimeParsing.dateToTimeString(current.getStartDate()) + "-" + DateTimeParsing.dateToTimeString(current.getEndDate());
                currentDate.setText(s);
            }
            if (previous != null) {
                previousDate.setText(DateTimeParsing.dateToDateString(previous.getStartDate()));
            }
        }
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
        finish();
    }
}