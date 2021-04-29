package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.applandeo.materialcalendarview.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.SelectedDay;
import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class DoctorAppointmentScheduleActivity extends AppCompatActivity {

    private int id;
    private Doctor doctor;

    private ArrayList<Appointment> appointments;
    private ArrayList<Appointment> allAvailableAppointments;
    private ArrayList<Appointment> visibleAppointments;

    private Calendar currentDay;
    private Calendar minDay;
    private Calendar maxDay;

    private TextView dateDisplay;
    private Button prev;
    private Button next;
    private RecyclerView recyclerView;
    private DoctorAppointmentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        doctor = (Doctor) getIntent().getParcelableExtra("doctor");
        id = doctor.getId();

        dateDisplay = findViewById(R.id.date_display);
        prev = findViewById(R.id.previous_date_button);
        next = findViewById(R.id.next_day_button);

        minDay = Calendar.getInstance();
        if (minDay.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || minDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            minDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        minDay.set(Calendar.HOUR_OF_DAY, 0);
        minDay.set(Calendar.MINUTE, 0);
        minDay.set(Calendar.SECOND, 0);
        currentDay = (Calendar)minDay.clone();

        ArrayList<Calendar> disabledDays = new ArrayList<>();
        Calendar test = (Calendar)minDay.clone();
        test.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        for (int i = 0; i < 100; i++) {
            disabledDays.add((Calendar)test.clone());
            test.add(Calendar.DAY_OF_WEEK, 1);
            disabledDays.add((Calendar)test.clone());
            test.add(Calendar.DAY_OF_WEEK, 6);
        }

        Calendar min = (Calendar) minDay.clone();
        min.add(Calendar.DAY_OF_MONTH, -1);
        Calendar max = (Calendar) min.clone();
        max.add(Calendar.YEAR, 1);
        max.set(Calendar.MONTH, Calendar.DECEMBER);
        max.set(Calendar.DAY_OF_MONTH, 25);
        maxDay = (Calendar) max.clone();

        setDate(minDay);

        DatePickerBuilder builder = new DatePickerBuilder(
                this,
                new OnSelectDateListener() {
                    @Override
                    public void onSelect(List<Calendar> calendar) {
                        setDate(calendar.get(0));
                    }
                })
                .setPickerType(CalendarView.ONE_DAY_PICKER)
                .setMinimumDate(min)
                .setMaximumDate(max)
                .setHeaderColor(R.color.dark_blue)
                .setTodayLabelColor(R.color.dark_blue)
                .setSelectionColor(R.color.blue)
                .setDisabledDays(disabledDays);

        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = builder
                        .setDate(currentDay)
                        .build();
                datePicker.show();
            }
        });

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

    public void prevDay(View view) {
        Calendar prevDate = (Calendar)currentDay.clone();
        prevDate.add(Calendar.DAY_OF_MONTH, -1);
        setDate(prevDate);
    }

    public void nextDay(View view) {
        Calendar prevDate = (Calendar)currentDay.clone();
        prevDate.add(Calendar.DAY_OF_MONTH, 1);
        setDate(prevDate);
    }

    private void setDate(Calendar newDate) {
        // Going from Friday to Saturday
        if (newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            newDate.add(Calendar.DAY_OF_WEEK, 2);
        }
        // Going from Monday to Sunday
        else if (newDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            newDate.add(Calendar.DAY_OF_WEEK, -2);
        }

        prev.setEnabled(newDate.getTimeInMillis() > minDay.getTimeInMillis());

        Calendar maxCheck = (Calendar) newDate.clone();
        // Next day can't be accessed
        maxCheck.add(Calendar.DAY_OF_WEEK, 1);
        boolean check1 = newDate.getTimeInMillis() < maxDay.getTimeInMillis();
        // The day right after the weekend can't be accessed
        maxCheck.add(Calendar.DAY_OF_WEEK, 2);
        boolean check2 = maxCheck.getTimeInMillis() < maxDay.getTimeInMillis();
        next.setEnabled(check1 && check2);

        currentDay = (Calendar)newDate.clone();
        dateDisplay.setText(DateTimeParsing.dateToDateString(currentDay.getTime()));
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