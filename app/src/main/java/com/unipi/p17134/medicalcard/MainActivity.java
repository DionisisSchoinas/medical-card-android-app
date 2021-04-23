package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Custom.VerificationPopup;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Listeners.VerificationPopupListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends ConnectedBaseClass implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView fullnameDisplay;
    private RecyclerView appointmentsDisplay;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;
    private PatientAppointmentsAdapter mAdapter;

    private ArrayList<Appointment> appointments;
    private ArrayList<RecycleViewItem> recycleViewItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        appointments = new ArrayList<>();
        recycleViewItems = new ArrayList<>();
        appointmentsDisplay = findViewById(R.id.mainAppointmentDisplay);
        appointmentsDisplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)appointmentsDisplay.getLayoutManager();
        mAdapter = new PatientAppointmentsAdapter(this, recycleViewItems, new ClickListener() {
            @Override
            public void onMoreInfoClicked(int index) {
                moreAppointmentInfo(index);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        appointmentsDisplay.setAdapter(mAdapter);

        Activity activity = this;
        DAOResponseListener responseListener = new DAOResponseListener() {
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
        PatientDAO.appointments(activity, 1, responseListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fullnameDisplay.setText(MyPrefs.getUserData(this).getFullname());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_doctor_search) {
            Toast.makeText(this, "Doctor Search", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_qr_generate) {
            Toast.makeText(this, "Generate QR", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_qr_read) {
            Toast.makeText(this, "Read QR", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_my_account) {
            Toast.makeText(this, "My Account", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_my_appointments) {
            Toast.makeText(this, "My Appointments", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.nav_login_register) {
            logout();
        }
        else if (id == R.id.nav_logout) {
            logout();
        }

        return true;
    }

    private void logout() {
        Activity activity = this;
        VerificationPopup.showPopup(this,
                getResources().getString(R.string.logout_popup_title),
                getResources().getString(R.string.logout_popup_message),
                getResources().getString(R.string.popup_positive),
                getResources().getString(R.string.popup_negative),
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void plusButtonClick() {
        Toast.makeText(this, "Plus pressed", Toast.LENGTH_SHORT).show();
    }

    private void moreAppointmentInfo(int index) {
        if (recycleViewItems.size() == 0)
            return;

        Appointment appointment = recycleViewItems.get(index).getAppointmentData();
        Intent intent = new Intent(getApplicationContext(), AppointmentDetailsActivity.class);
        intent.putExtra("id", appointment.getId());
        startActivityForResult(intent, MyPermissions.RESPONSE_FROM_APPOINTMENT_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyPermissions.RESPONSE_FROM_APPOINTMENT_INFO && resultCode == RESULT_OK) {
            int id = 0;
            if (data != null)
                id = data.getIntExtra("id", 0);

            renameAppointmentWithId(id);
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
                    item.setDateSplitterType("Today");
                }
                else {
                    item.setDateSplitterType(currentDate);
                }
                recycleViewItems.add(item);
                lastDate = currentDate;
            }

            // Add appointment item
            RecycleViewItem item = new RecycleViewItem();
            item.setPatientAppointmentType(appointment);
            recycleViewItems.add(item);
        }

        // Fill display with appointments
        // Create adapter passing in the sample user data
        if (appointmentsDisplay.getAdapter() != null)
            appointmentsDisplay.getAdapter().notifyDataSetChanged();
    }

    private void renameAppointmentWithId(int id) {
        // Go through appointments to find specific appointment
        for (int i=0; i<appointments.size(); i++) {
            // WHen found
            if (appointments.get(i).getId() == id) {
                // Go through recycle view items to find date splitter for this item's date
                Appointment appointment = appointments.get(i);
                for (int j=0; j<recycleViewItems.size(); j++) {
                    // When found
                    if (recycleViewItems.get(j).getItemType() == RecycleViewItem.DATE_SPLITTER && recycleViewItems.get(j).getDateString().equals(DateTimeParsing.dateToDateString(appointment.getStartDate()))) {
                        // Go through recycle view items to find appointment display with specified id
                        int count = 0;
                        int index = j;
                        for (int k=j+1; k<recycleViewItems.size(); k++) {
                            // If finished going through this days appointments ( found another date splitter )
                            if (recycleViewItems.get(k).getItemType() == RecycleViewItem.DATE_SPLITTER)
                                break;

                            // When found
                            if (recycleViewItems.get(k).getAppointmentData().getId() == id) {
                                index = k;
                            }
                            count++;
                        }
                        recycleViewItems.remove(index);
                        // If only one appointment existed for this date, remove the date splitter as well
                        if (count == 1)
                            recycleViewItems.remove(j);
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
}