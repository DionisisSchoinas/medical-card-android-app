package com.unipi.p17134.medicalcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.Adapters.DoctorListAdapter;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.util.ArrayList;

public class DoctorListActivity extends ConnectedBaseClass {
    private ArrayList<Doctor> doctorList;
    private RecyclerView recyclerView;
    private DoctorListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.doctor_list_display);

        doctorList = new ArrayList<>();
        recyclerView = findViewById(R.id.mainAppointmentDisplay);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new DoctorListAdapter(this, doctorList, new ClickListener() {
            @Override
            public void onClick(int index) {
                addAppointment(index);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(mAdapter);
    }

    private void addAppointment(int index) {

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