package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Patient;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class DoctorAppointmentListActivity extends ConnectedBaseClass {
    private RecyclerView appointmentsDisplay;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;
    private DoctorAppointmentsAdapter mAdapter;
    private ArrayList<Appointment> appointments;
    private ArrayList<RecyclerViewItem> recyclerViewItems;
    private DAOResponseListener responseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_list);

        DoctorDAO.resetCounters();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Activity activity = this;
        responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                processNewAppointments((ArrayList<Appointment>)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        };

        appointments = new ArrayList<>();
        recyclerViewItems = new ArrayList<>();
        appointmentsDisplay = findViewById(R.id.doctor_appointment_list_display);
        appointmentsDisplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)appointmentsDisplay.getLayoutManager();
        mAdapter = new DoctorAppointmentsAdapter(recyclerViewItems, new ClickListener() {
            @Override
            public void onClick(int index) {
                loadingDialog.startLoadingDialog();
                DoctorDAO.appointments(activity, -2, responseListener);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        appointmentsDisplay.setAdapter(mAdapter);

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

        processNewAppointments(new ArrayList<>());
        DoctorDAO.appointments(activity, 1, responseListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.startLoadingDialog();
        FirebaseMessaging.getInstance().subscribeToTopic("extra"+MyPrefs.getDoctorId(this));
    }

    @Override
    protected void onStop() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("extra"+MyPrefs.getDoctorId(this));
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() == null)
            return;
        if (remoteMessage.getNotification().getBody() == null)
            return;

        if (remoteMessage.getNotification().getBody().equals("DELETE")) {
            removeAppointment(remoteMessage);
        }
        else if (remoteMessage.getNotification().getBody().equals("CREATE")) {
            addAppointment(remoteMessage);
        }
    }

    private void addAppointment(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() == 0)
            return;

        Appointment appointment = new Appointment();
        try {
            appointment.setId(Integer.parseInt(remoteMessage.getData().get("id")));
            appointment.setStartDate(DateTimeParsing.APIDateToDate(remoteMessage.getData().get("appointment_date_time_start")));
            appointment.setEndDate(DateTimeParsing.APIDateToDate(remoteMessage.getData().get("appointment_date_time_end")));
            appointment.setPatient(
                    new Patient(
                            0,
                            new User()
                                .setFullname(remoteMessage.getData().get("patient_fullname"))
                    )
            );

            if (appointments.size() == 0) {
                ArrayList<Appointment> appointments = new ArrayList<>();
                appointments.add(appointment);
                processNewAppointments(appointments);
                return;
            }

            // If new appointment before the last visible date
            if (appointment.getStartDate().before(recyclerViewItems.get(recyclerViewItems.size()-1).getAppointmentData().getStartDate())) {
                for (int i=0; i < appointments.size(); i++) {
                    // Starting from the first appointment, check if new appointment before the current appointment
                    // Appointments are always in ascending order based on time
                    if (appointment.getStartDate().before(appointments.get(i).getStartDate())) {
                        // Delete deleted appointment
                        appointments.add(i, appointment);
                        // Save copy of preloaded appointments
                        ArrayList<Appointment> appointmentsCopy = new ArrayList<>(appointments);
                        // CLear pre loaded items and appointments
                        recyclerViewItems.clear();
                        appointments.clear();
                        // Redisplay loaded appointments
                        processNewAppointments(appointmentsCopy);
                        break;
                    }
                }
                return;
            }
            // If current appointments are exactly 20, 40, 60, 80, ... this means the new item is in the next page so pull it
            if (appointments.size() % 20 == 0)
                DoctorDAO.appointments(this, -2, responseListener);
            // If current appointments are not this means the new item is in the current page
            else
                DoctorDAO.appointments(this, -3, responseListener);
        }
        catch (Exception ignored) {
        }
    }

    private void removeAppointment(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() == 0)
            return;

        Appointment appointment = new Appointment();
        try {
            appointment.setId(Integer.parseInt(remoteMessage.getData().get("id")));

            for (int i=0; i < appointments.size(); i++) {
                if (appointments.get(i).getId() == appointment.getId()) {
                    // Delete deleted appointment
                    appointments.remove(i);
                    // Save copy of preloaded appointments
                    ArrayList<Appointment> appointmentsCopy = new ArrayList<>(appointments);
                    // CLear pre loaded items and appointments
                    recyclerViewItems.clear();
                    appointments.clear();
                    // Redisplay loaded appointments
                    processNewAppointments(appointmentsCopy);
                    break;
                }
            }
        }
        catch (Exception ignored) {
        }
    }

    private boolean isDuplicate(ArrayList<Appointment> appointments, Appointment newAppointment) {
        for (Appointment app:appointments) {
            if (app.getId() == newAppointment.getId())
                return true;
        }
        return false;
    }

    private void processNewAppointments(ArrayList<Appointment> newAppointments) {
        // Remove the end of list row
        if (recyclerViewItems.size() != 0 && recyclerViewItems.get(recyclerViewItems.size()-1).getItemType() == RecyclerViewItem.END_OF_LIST)
            recyclerViewItems.remove(recyclerViewItems.size()-1);

        // Find last date if it exists
        String lastDate;
        if (recyclerViewItems.size() == 0)
            lastDate = "";
        else
            lastDate = DateTimeParsing.dateToDateString(recyclerViewItems.get(recyclerViewItems.size()-1).getAppointmentData().getStartDate());

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
                RecyclerViewItem item = new RecyclerViewItem();
                if (DateTimeParsing.currentDate().equals(currentDate)) {
                    item.setDateSplitterType(getResources().getString(R.string.today));
                }
                else {
                    item.setDateSplitterType(currentDate);
                }
                recyclerViewItems.add(item);
                lastDate = currentDate;
            }

            // Add appointment item
            RecyclerViewItem item = new RecyclerViewItem();
            item.setDoctorAppointmentType(appointment);
            recyclerViewItems.add(item);
        }

        // Add end of list row
        if (recyclerViewItems.size() == 0) {
            RecyclerViewItem endOfList = new RecyclerViewItem();
            endOfList.setEndOfListType();
            recyclerViewItems.add(endOfList);
        }

        // Fill display with appointments
        mAdapter.notifyDataSetChanged();
    }
}