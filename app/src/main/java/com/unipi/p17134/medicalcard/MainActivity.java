package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Custom.VerificationPopup;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Listeners.VerificationPopupListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.util.ArrayList;

public class MainActivity extends ConnectedBaseClass implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView fullnameDisplay;

    private RecyclerView appointmentsDisplay;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;
    private PatientAppointmentsAdapter mAdapter;
    private ArrayList<Appointment> appointments;
    private ArrayList<RecyclerViewItem> recyclerViewItems;
    private DAOResponseListener responseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PatientDAO.resetCounters();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.main_activity));

        FloatingActionButton fab = findViewById(R.id.plusButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusButtonClick();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fullnameDisplay = navigationView.getHeaderView(0).findViewById(R.id.fullnameActionBarDisplay);
        // Hide or Show functions meant only for doctors
        navigationView.getMenu().findItem(R.id.nav_qr_read).setVisible(MyPrefs.isDoctor(this));
        navigationView.getMenu().findItem(R.id.nav_my_account).setVisible(MyPrefs.isDoctor(this));
        navigationView.getMenu().findItem(R.id.nav_my_appointments).setVisible(MyPrefs.isDoctor(this));


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
        appointmentsDisplay = findViewById(R.id.mainAppointmentDisplay);
        appointmentsDisplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)appointmentsDisplay.getLayoutManager();
        mAdapter = new PatientAppointmentsAdapter(recyclerViewItems, new ClickListener() {
            @Override
            public void onClick(int index) {
                moreAppointmentInfo(index);
            }
        }, new ClickListener() {
            @Override
            public void onClick(int index) {
                loadingDialog.startLoadingDialog();
                PatientDAO.appointments(activity, -2, responseListener);
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
                    PatientDAO.appointments(activity, -1, responseListener);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (appointmentsDisplay.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (currentDisplayState == RecyclerView.SCROLL_STATE_DRAGGING && dy > 0 && layoutManager.findLastVisibleItemPosition() >= appointmentsDisplay.getAdapter().getItemCount() - 10) {
                    PatientDAO.appointments(activity, -1, responseListener);
                }
            }
        });

        processNewAppointments(new ArrayList<>());
        PatientDAO.appointments(activity, 1, responseListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fullnameDisplay.setText(MyPrefs.getUserData(this).getFullname());

        loadingDialog.startLoadingDialog();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_doctor_search) {
            doctorList();
        }
        else if (id == R.id.nav_qr_generate) {
            startActivity(new Intent(this, GenerateQRActivity.class));
        }
        else if (id == R.id.nav_qr_read) {
            startActivity(new Intent(this, ReadQRActivity.class));
        }
        else if (id == R.id.nav_my_account) {
            startActivity(new Intent(this, MyAccountActivity.class));
        }
        else if (id == R.id.nav_my_appointments) {
            startActivity(new Intent(this, DoctorAppointmentListActivity.class));
        }
        else if (id == R.id.nav_login_register) {
            logout();
        }
        else if (id == R.id.nav_logout) {
            logout();
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void logout() {
        Activity activity = this;
        VerificationPopup.showPopup(this,
                getResources().getString(R.string.logout_popup_title),
                getResources().getString(R.string.logout_popup_message),
                getResources().getString(R.string.popup_positive_yes),
                getResources().getString(R.string.popup_negative_no),
                new VerificationPopupListener() {
                    @Override
                    public void onPositive() {
                        MyPrefs.logout(activity);
                    }

                    @Override
                    public void onNegative() {

                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void plusButtonClick() {
        doctorList();
    }

    private void moreAppointmentInfo(int index) {
        if (recyclerViewItems.size() == 0)
            return;

        Appointment appointment = recyclerViewItems.get(index).getAppointmentData();
        Intent intent = new Intent(getApplicationContext(), AppointmentDetailsActivity.class);
        intent.putExtra("id", appointment.getId());
        startActivityForResult(intent, MyPermissions.RESPONSE_FROM_APPOINTMENT_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyPermissions.RESPONSE_FROM_APPOINTMENT_INFO && resultCode == RESULT_OK) {
            recreate();
        }
        else if (requestCode == MyPermissions.RESPONSE_FROM_DOCTOR_LIST && resultCode == RESULT_OK) {
            recreate();
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
            item.setPatientAppointmentType(appointment);
            recyclerViewItems.add(item);
        }

        // Add end of list row
        if (recyclerViewItems.size() == 0) {
            RecyclerViewItem endOfList = new RecyclerViewItem();
            endOfList.setEndOfListType();
            recyclerViewItems.add(endOfList);
        }

        // Fill display with appointments
        // Create adapter passing in the sample user data
        if (appointmentsDisplay.getAdapter() != null)
            appointmentsDisplay.getAdapter().notifyDataSetChanged();
    }
/*
    private void removeAppointmentWithId(int id) {
        // Go through appointments to find specific appointment
        for (int i=0; i<appointments.size(); i++) {
            // WHen found
            if (appointments.get(i).getId() == id) {
                // Go through recycle view items to find date splitter for this item's date
                Appointment appointment = appointments.get(i);
                for (int j=0; j<recyclerViewItems.size(); j++) {
                    // When found
                    if (recyclerViewItems.get(j).getItemType() == RecyclerViewItem.DATE_SPLITTER && recyclerViewItems.get(j).getDateString().equals(DateTimeParsing.dateToDateString(appointment.getStartDate()))) {
                        // Go through recycle view items to find appointment display with specified id
                        int count = 0;
                        int index = j;
                        for (int k=j+1; k<recyclerViewItems.size(); k++) {
                            // If finished going through this days appointments ( found another date splitter )
                            if (recyclerViewItems.get(k).getItemType() == RecyclerViewItem.DATE_SPLITTER)
                                break;

                            // When found
                            if (recyclerViewItems.get(k).getAppointmentData().getId() == id) {
                                index = k;
                            }
                            count++;
                        }
                        recyclerViewItems.remove(index);
                        // If only one appointment existed for this date, remove the date splitter as well
                        if (count == 1)
                            recyclerViewItems.remove(j);
                        break;
                    }
                }
                appointments.remove(i);
                break;
            }
        }
        appointmentsDisplay.getAdapter().notifyDataSetChanged();
        Toast.makeText(this, getResources().getString(R.string.appointment_list_updated), Toast.LENGTH_SHORT).show();
    }
*/
    private void doctorList() {
        startActivityForResult(new Intent(this, DoctorListActivity.class), MyPermissions.RESPONSE_FROM_DOCTOR_LIST);
    }
}