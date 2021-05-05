package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.applandeo.materialcalendarview.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorScheduleAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Custom.VerificationPopup;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Listeners.VerificationPopupListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DoctorAppointmentScheduleActivity extends AppCompatActivity {
    private int id;
    private Doctor doctor;

    private HashMap<String, HashMap<String, ArrayList<Appointment>>> appointmentsMap;
    private ArrayList<RecycleViewItem> visibleAppointments;
    private DAOResponseListener responseListener;

    private Calendar currentDay;
    private Calendar minDay;
    private Calendar maxDay;

    private TextView dateDisplay;
    private Button prev;
    private Button next;
    private RecyclerView recyclerView;
    private DoctorScheduleAppointmentsAdapter mAdapter;

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

        appointmentsMap = new HashMap<>();
        visibleAppointments = new ArrayList<>();
        generateTodayVisibleItems();

        recyclerView = findViewById(R.id.appointmentDisplay);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), calculateNoOfColumns(120)));
        mAdapter = new DoctorScheduleAppointmentsAdapter(this, visibleAppointments, new ClickListener() {
            @Override
            public void onClick(int index) {
                bookAppointment(index);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(mAdapter);

        responseListener = new DAOResponseListener() {
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

        // Check if month has been changed
        if (currentDay == null || newDate.get(Calendar.MONTH) != currentDay.get(Calendar.MONTH)) {
            monthChanged(newDate);
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

        updateAppointmentsView();
    }

    private void bookAppointment(int index) {
        RecycleViewItem item = visibleAppointments.get(index);
        if (item.isBooked())
            return;

        String message =
                getResources().getString(R.string.book_appointment_popup_message) + "\n\n" +
                doctor.getUser().getFullname() + "\n" +
                doctor.getOfficeAddress() + "\n\n" +
                DateTimeParsing.dateToDateString(currentDay.getTime()) + ", " +
                DateTimeParsing.dateToTimeString(item.getAppointmentData().getStartDate()) + "-" +
                DateTimeParsing.dateToTimeString(item.getAppointmentData().getEndDate());

        Activity activity = this;
        VerificationPopup.showPopup(
                this,
                getResources().getString(R.string.book_appointment_popup_title),
                message,
                getResources().getString(R.string.popup_positive_confirm),
                getResources().getString(R.string.popup_negative_cancel),
                new VerificationPopupListener() {
                    @Override
                    public void onPositive() {
                        Appointment appointment = new Appointment();
                        appointment.setDoctor(doctor);

                        Calendar cal1 = (Calendar) currentDay.clone();
                        Calendar cal2 = (Calendar) currentDay.clone();
                        Calendar finalCalendar1 = (Calendar) currentDay.clone();
                        Calendar finalCalendar2 = (Calendar) currentDay.clone();
                        // Set start date
                        cal1.setTime(item.getAppointmentData().getStartDate());
                        finalCalendar1.set(Calendar.HOUR_OF_DAY, cal1.get(Calendar.HOUR_OF_DAY));
                        finalCalendar1.set(Calendar.MINUTE, cal1.get(Calendar.MINUTE));
                        appointment.setStartDate(finalCalendar1.getTime());
                        // Set start date
                        cal2.setTime(item.getAppointmentData().getEndDate());
                        finalCalendar2.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
                        finalCalendar2.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
                        appointment.setEndDate(finalCalendar2.getTime());

                        PatientDAO.bookAppointment(activity, appointment, new DAOResponseListener() {
                            @Override
                            public <T> void onResponse(T object) {
                                Toast.makeText(getApplicationContext(), R.string.book_appointment_success, Toast.LENGTH_SHORT).show();

                                setResult(RESULT_OK, new Intent());
                                finish();
                            }

                            @Override
                            public <T> void onErrorResponse(T error) {
                                Toast.makeText(getApplicationContext(), R.string.book_appointment_failure, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNegative() {
                        Toast.makeText(getApplicationContext(), R.string.book_appointment_failure, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void monthChanged(Calendar newMonth) {
        // Previous month
        Calendar prevCal = (Calendar) newMonth.clone();
        prevCal.add(Calendar.MONTH, -1);
        // next month
        Calendar nextCal = (Calendar) newMonth.clone();
        nextCal.add(Calendar.MONTH, 1);

        String newMonthString = DateTimeParsing.dateToMonthString(newMonth.getTime());
        String prevMonthString = DateTimeParsing.dateToMonthString(prevCal.getTime());
        String nextMonthString = DateTimeParsing.dateToMonthString(nextCal.getTime());

        // If a registered month doesn't match any of the new months, remove it
        List<String> keys = new ArrayList<>(appointmentsMap.keySet());
        for (String key : keys) {
            if (key.equals(newMonthString))
                continue;

            if (key.equals(prevMonthString))
                continue;

            if (key.equals(nextMonthString))
                continue;

            MyRequestHandler.getInstance(this).getRequestQueue(this).cancelAll(key);
            appointmentsMap.remove(key);
        }

        // Current month - if not already registered
        if (!appointmentsMap.containsKey(newMonthString)) {
            appointmentsMap.put(newMonthString, new HashMap<>());
            DoctorDAO.simple_appointments(this, 1, id, newMonth, true, responseListener);
        }

        // Previous month - if not already registered
        if (!appointmentsMap.containsKey(prevMonthString)) {
            appointmentsMap.put(prevMonthString, new HashMap<>());
            DoctorDAO.simple_appointments(this, 1, id, prevCal, true, responseListener);
        }

        // Next month - if not already registered
        if (!appointmentsMap.containsKey(nextMonthString)) {
            appointmentsMap.put(nextMonthString, new HashMap<>());
            DoctorDAO.simple_appointments(this, 1, id, nextCal, true, responseListener);
        }
    }

    private boolean isDuplicate(ArrayList<Appointment> appointments, Appointment newAppointment) {
        for (Appointment app : appointments) {
            if (app.getId() == newAppointment.getId())
                return true;
        }
        return false;
    }

    private void newAppointments(ArrayList<Appointment> newAppointments) {
        String monthString = "";
        String dayString = "";
        Appointment appointment;
        boolean updateView = false;
        for (int i=0; i<newAppointments.size(); i++) {
            // Get appointment
            appointment = newAppointments.get(i);
            // Get date strings
            monthString = DateTimeParsing.dateToMonthString(appointment.getStartDate());
            dayString = DateTimeParsing.dateToDayString(appointment.getStartDate());

            if (!appointmentsMap.containsKey(monthString))
                break;

            // Add day if not already registered
            if (!appointmentsMap.get(monthString).containsKey(dayString)) {
                appointmentsMap
                        .get(monthString)
                        .put(dayString, new ArrayList<>());
            }

            // If appointment already registered
            if (isDuplicate(appointmentsMap.get(monthString).get(dayString), appointment))
                continue;

            appointmentsMap.get(monthString).get(dayString).add(appointment);

            // If the any of the appointments added have the same date as the currently displayed date, update the view
            if (DateTimeParsing.dateToDateString(appointment.getStartDate()).equals(DateTimeParsing.dateToDateString(currentDay.getTime())))
                updateView = true;
        }

        if (updateView)
            updateAppointmentsView();
    }

    private ArrayList<Appointment> generateAvailableAppointments() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        ArrayList<Appointment> appointments = new ArrayList<>();
        for (int i=0; i<4; i++) {
            Appointment appointment = new Appointment();
            appointment.setStartDate(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            appointment.setEndDate(calendar.getTime());

            appointments.add(appointment);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        for (int i=0; i<6; i++) {
            Appointment appointment = new Appointment();
            appointment.setStartDate(calendar.getTime());
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            appointment.setEndDate(calendar.getTime());

            appointments.add(appointment);
        }

        return appointments;
    }

    private void generateTodayVisibleItems() {
        ArrayList<Appointment> appointments = generateAvailableAppointments();

        Calendar calendar = (Calendar) minDay.clone();
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        calendar.add(Calendar.HOUR_OF_DAY, 6);

        visibleAppointments.clear();
        for (Appointment appointment : appointments) {
            if (appointment.getStartDate().after(calendar.getTime())) {
                RecycleViewItem item = new RecycleViewItem();
                item.setDoctorScheduleItem(appointment, false);
                visibleAppointments.add(item);
            }
        }
    }

    private void generateVisibleItems() {
        ArrayList<Appointment> appointments = generateAvailableAppointments();

        visibleAppointments.clear();;
        for (Appointment appointment : appointments) {
            RecycleViewItem item = new RecycleViewItem();
            item.setDoctorScheduleItem(appointment, false);
            visibleAppointments.add(item);
        }
    }

    private void updateAppointmentsView() {
        String month = DateTimeParsing.dateToMonthString(currentDay.getTime());
        String day = DateTimeParsing.dateToDayString(currentDay.getTime());

        ArrayList<Appointment> bookedAppointments = appointmentsMap.get(month).get(day);

        Calendar testCal1 = Calendar.getInstance();
        Calendar testCal2 = Calendar.getInstance();
/*
        if (DateTimeParsing.dateToDateString(currentDay.getTime()).equals(DateTimeParsing.dateToDateString(minDay.getTime())))
            visibleAppointments = generateTodayVisibleItems();
        else
            visibleAppointments = allAvailableAppointments;

 */
        if (DateTimeParsing.dateToDateString(currentDay.getTime()).equals(DateTimeParsing.dateToDateString(minDay.getTime())))
            generateTodayVisibleItems();
        else
            generateVisibleItems();

        for (int i=0; i<visibleAppointments.size(); i++) {
            RecycleViewItem item = visibleAppointments.get(i);
            item.isBooked(false);
            if (bookedAppointments == null)
                continue;

            testCal1.setTime(item.getAppointmentData().getStartDate());
            int itemTime = testCal1.get(Calendar.HOUR_OF_DAY);

            for (Appointment appointment : bookedAppointments) {
                testCal2.setTime(appointment.getStartDate());
                int appointmentTime = testCal2.get(Calendar.HOUR_OF_DAY);

                if (itemTime == appointmentTime) {
                    item.isBooked(true);
                    break;
                }
            }
        }

        mAdapter.notifyDataSetChanged();
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