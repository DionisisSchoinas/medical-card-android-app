package com.unipi.p17134.medicalcard;

import android.app.Activity;
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
import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends ConnectedBaseClass implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView fullnameDisplay;
    private RecyclerView appointmentsDisplay;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;

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

        appointmentsDisplay = findViewById(R.id.mainAppointmentDisplay);
        appointmentsDisplay.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)appointmentsDisplay.getLayoutManager();
        Activity activity = this;

        ClickListener clickListener =  new ClickListener() {
            @Override
            public void onMoreInfoClicked(int index) {
                moreAppointmentInfo(index);
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
                    PatientDAO.appointments(activity, appointmentsDisplay, clickListener, -1);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (appointmentsDisplay.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (currentDisplayState == RecyclerView.SCROLL_STATE_DRAGGING && dy > 0 && layoutManager.findLastVisibleItemPosition() >= appointmentsDisplay.getAdapter().getItemCount() - 10) {
                    PatientDAO.appointments(activity, appointmentsDisplay,clickListener, -1);
                }
            }
        });
        PatientDAO.appointments(this, appointmentsDisplay,clickListener, 1);
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
            UserDAO.logout(this);
        }
        else if (id == R.id.nav_logout) {
            UserDAO.logout(this);
        }

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
        PatientAppointmentsAdapter adapter = (PatientAppointmentsAdapter)appointmentsDisplay.getAdapter();
        if (adapter == null)
            return;

        Appointment appointment = adapter.getDataset().get(index).getAppointmentData();
        Intent intent = new Intent(getApplicationContext(), AppointmentDetailsActivity.class);
        intent.putExtra("id", appointment.getId());
        startActivity(intent);
    }
}