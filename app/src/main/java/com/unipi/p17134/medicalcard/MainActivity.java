package com.unipi.p17134.medicalcard;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.custom.MyPrefs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends ConnectedBaseClass implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private TextView fullnameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.main_activity);

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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void plusButtonClick() {
        Toast.makeText(this, "Plus pressed", Toast.LENGTH_SHORT).show();
    }
}