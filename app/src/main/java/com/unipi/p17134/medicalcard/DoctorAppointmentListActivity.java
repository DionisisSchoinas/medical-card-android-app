package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.util.ArrayList;

public class DoctorAppointmentListActivity extends AppCompatActivity {
    private RecyclerView appointmentsDisplay;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;
    private DoctorAppointmentsAdapter mAdapter;
    private ArrayList<Appointment> appointments;
    private ArrayList<RecycleViewItem> recycleViewItems;
    private DAOResponseListener responseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_list);

        DoctorDAO.resetCounters();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appointments = new ArrayList<>();
        recycleViewItems = new ArrayList<>();
        appointmentsDisplay = findViewById(R.id.doctor_appointment_list_display);
        appointmentsDisplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)appointmentsDisplay.getLayoutManager();
        mAdapter = new DoctorAppointmentsAdapter(this, recycleViewItems);
        // Attach the adapter to the recyclerview to populate items
        appointmentsDisplay.setAdapter(mAdapter);

        Activity activity = this;
        responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                processNewAppointments((ArrayList<Appointment>)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        };

        appointmentsDisplay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                currentDisplayState = newState;

                if (appointmentsDisplay.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (layoutManager.findLastVisibleItemPosition() >= appointmentsDisplay.getAdapter().getItemCount() - 10) {
                    DoctorDAO.appointments(activity, -1, responseListener);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (appointmentsDisplay.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (currentDisplayState == RecyclerView.SCROLL_STATE_DRAGGING && dy > 0 && layoutManager.findLastVisibleItemPosition() >= appointmentsDisplay.getAdapter().getItemCount() - 10) {
                    DoctorDAO.appointments(activity, -1, responseListener);
                }
            }
        });
        DoctorDAO.appointments(activity, 1, responseListener);
    }

    private boolean isDuplicate(ArrayList<Appointment> appointments, Appointment newAppointment) {
        for (Appointment app:appointments) {
            if (app.getId() == newAppointment.getId())
                return true;
        }
        return false;
    }

    private void processNewAppointments(ArrayList<Appointment> newAppointments) {
        if (newAppointments.size() == 0)
            return;

        // Find last date if it exists
        String lastDate;
        if (recycleViewItems.size() == 0)
            lastDate = "";
        else
            lastDate = DateTimeParsing.dateToDateString(recycleViewItems.get(recycleViewItems.size()-1).getAppointmentData().getStartDate());

        Appointment appointment;
        for (int i=0; i<newAppointments.size(); i++) {
            appointment = newAppointments.get(i);
            // If appointment already exists skip it
            if (isDuplicate(appointments, appointment))
                continue;

            appointments.add(appointment);

            // Check if date has changed
            // If it has add a date splitter
            String currentDate = DateTimeParsing.dateToDateString(appointment.getStartDate());
            if (!currentDate.equals(lastDate)) {
                RecycleViewItem item = new RecycleViewItem();
                if (DateTimeParsing.currentDate().equals(currentDate)) {
                    item.setDateSplitterType(getResources().getString(R.string.today));
                }
                else {
                    item.setDateSplitterType(currentDate);
                }
                recycleViewItems.add(item);
                lastDate = currentDate;
            }

            // Add appointment item
            RecycleViewItem item = new RecycleViewItem();
            item.setDoctorAppointmentType(appointment);
            recycleViewItems.add(item);
        }

        // Fill display with appointments
        // Create adapter passing in the sample user data
        if (appointmentsDisplay.getAdapter() != null)
            appointmentsDisplay.getAdapter().notifyDataSetChanged();
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