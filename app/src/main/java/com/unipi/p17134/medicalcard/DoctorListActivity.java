package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorListAdapter;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.util.ArrayList;

public class DoctorListActivity extends ConnectedBaseClass {
    private ArrayList<Doctor> doctors;
    private RecyclerView recyclerView;
    private DoctorListAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;

    private ConstraintLayout searchView;
    private EditText specialitySearch;

    private String currentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Activity activity = this;
        searchView = findViewById(R.id.searchView);
        specialitySearch = findViewById(R.id.specialitySearch);
        hideSearchView();

        doctors = new ArrayList<>();
        recyclerView = findViewById(R.id.doctor_list_display);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        mAdapter = new DoctorListAdapter(doctors, new ClickListener() {
            @Override
            public void onClick(int index) {
                addAppointment(index);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(mAdapter);

        DAOResponseListener responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                processNewDoctors((ArrayList<Doctor>)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                currentDisplayState = newState;

                if (recyclerView.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (layoutManager.findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 10) {
                    DoctorDAO.doctors(activity, -1, specialitySearch.getText().toString(), responseListener);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (recyclerView.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (currentDisplayState == RecyclerView.SCROLL_STATE_DRAGGING && dy > 0 && layoutManager.findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 10) {
                    DoctorDAO.doctors(activity, -1, specialitySearch.getText().toString(), responseListener);
                }
            }
        });

        // Fill with 1 page of doctors
        DoctorDAO.doctors(activity, 1, specialitySearch.getText().toString(), responseListener);
    }

    public void searchWithFilter(View view) {
        DAOResponseListener filterResponseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                filterDoctors((ArrayList<Doctor>)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        };
        DoctorDAO.doctors(this, 1, specialitySearch.getText().toString(), filterResponseListener);
        currentFilter = specialitySearch.getText().toString();
        hideSearchView();
    }

    private void addAppointment(int index) {
        Toast.makeText(this, "Book with : " + doctors.get(index).getUser().getFullname(), Toast.LENGTH_SHORT).show();
    }

    private void processNewDoctors(ArrayList<Doctor> newDoctors) {
        if (newDoctors.size() == 0)
            return;

        Doctor doctor;
        for (int i=0; i<newDoctors.size(); i++) {
            doctor = newDoctors.get(i);
            // If appointment already exists skip it
            if (isDuplicate(doctors, doctor))
                continue;

            doctors.add(doctor);
        }

        // Fill display with appointments
        // Create adapter passing in the sample user data
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private boolean isDuplicate(ArrayList<Doctor> doctors, Doctor newDoctor) {
        for (Doctor doc:doctors) {
            if (doc.getId() == newDoctor.getId())
                return true;
        }
        return false;
    }

    private void filterDoctors(ArrayList<Doctor> filteredDoctors) {
        doctors.clear();
        processNewDoctors(filteredDoctors);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter, menu);
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
        else if (id == R.id.action_filter) {
            if (searchView.getVisibility() == View.VISIBLE)
                hideSearchView();
            else
                showSearchView();
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
        if (searchView.getVisibility() == View.VISIBLE) {
            hideSearchView();
            return;
        }

        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void showSearchView() {
        searchView.setVisibility(View.VISIBLE);
    }

    private void hideSearchView() {
        searchView.setVisibility(View.GONE);
        if (currentFilter != null)
            specialitySearch.setText(currentFilter);
    }
}