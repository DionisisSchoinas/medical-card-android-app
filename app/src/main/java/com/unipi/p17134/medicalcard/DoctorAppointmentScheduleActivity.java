package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class DoctorAppointmentScheduleActivity extends AppCompatActivity {

    private int id= 2;
    private ArrayList<Appointment> appointments;
    private ArrayList<Appointment> allAvailableAppointments;
    private ArrayList<Appointment> visibleAppointments;

    private Calendar currentDay;
    private TextView dateDisplay;
    private RecyclerView recyclerView;
    private DoctorAppointmentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentDay = Calendar.getInstance();
        dateDisplay = findViewById(R.id.date_display);
        dateDisplay.setText(DateTimeParsing.dateToDay(currentDay.getTime()));

        appointments = new ArrayList<>();
        allAvailableAppointments = generateAvailableAppointments();
        visibleAppointments = getVisibleAppointments();

        recyclerView = findViewById(R.id.appointmentDisplay);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), calculateNoOfColumns(120)));
        mAdapter = new DoctorAppointmentsAdapter(visibleAppointments, new ClickListener() {
            @Override
            public void onClick(int index) {
                bookAppointment(index);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(mAdapter);

        DAOResponseListener responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                ArrayList<Appointment> appointments = (ArrayList<Appointment>) object;
                newAppointments(appointments);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        };
        DoctorDAO.simple_appointments(this, 1, id, responseListener);
    }

    private void bookAppointment(int index) {

    }

    private boolean isDuplicate(ArrayList<Appointment> appointments, Appointment newAppointment) {
        for (Appointment app:appointments) {
            if (app.getId() == newAppointment.getId())
                return true;
        }
        return false;
    }

    private void newAppointments(ArrayList<Appointment> newAppointments) {
        Appointment appointment;
        for (int i=0; i<newAppointments.size(); i++) {
            appointment = newAppointments.get(i);
            // If appointment already exists skip it
            if (isDuplicate(appointments, appointment))
                continue;

            appointments.add(appointment);
        }

        visibleAppointments = getVisibleAppointments();
        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<Appointment> generateAvailableAppointments() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        ArrayList<Appointment> appointments = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Appointment appointment = new Appointment();
            appointment.setStartDate(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            appointment.setEndDate(calendar.getTime());

            appointments.add(appointment);
        }

        return appointments;
    }

    private ArrayList<Appointment> getVisibleAppointments() {
        return allAvailableAppointments;
    }

    private int calculateNoOfColumns(float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int)((screenWidthDp - 20) / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
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
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}