package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorListAdapter;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.util.ArrayList;

public class DoctorListActivity extends ConnectedBaseClass {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int currentDisplayState;
    private DoctorListAdapter mAdapter;
    private ArrayList<Doctor> doctors;
    private ArrayList<RecyclerViewItem> recyclerViewItems;
    private DAOResponseListener responseListener;

    private ConstraintLayout searchView;
    private EditText specialitySearch;

    private String currentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        DoctorDAO.resetCounters();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Activity activity = this;

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
        searchView = findViewById(R.id.searchView);
        specialitySearch = findViewById(R.id.specialitySearch);
        hideSearchView();

        doctors = new ArrayList<>();
        recyclerViewItems = new ArrayList<>();
        recyclerView = findViewById(R.id.doctor_list_display);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        mAdapter = new DoctorListAdapter(recyclerViewItems, new ClickListener() {
            @Override
            public void onClick(int index) {
                addAppointment(index);
            }
        }, new ClickListener() {
            @Override
            public void onClick(int index) {
                DoctorDAO.doctors(activity, -2, specialitySearch.getText().toString(), responseListener);
            }
        });
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(mAdapter);

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

        processNewDoctors(new ArrayList<>());
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
        DoctorDAO.resetCounters();
        DoctorDAO.doctors(this, 1, specialitySearch.getText().toString(), filterResponseListener);
        currentFilter = specialitySearch.getText().toString();
        hideSearchView();
    }

    private void addAppointment(int index) {
        Doctor doctor = recyclerViewItems.get(index).getDoctorData();
        Intent intent = new Intent(getApplicationContext(), DoctorDetailsActivity.class);
        intent.putExtra("id", doctor.getId());
        startActivityForResult(intent, MyPermissions.RESPONSE_FROM_DOCTOR_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyPermissions.RESPONSE_FROM_DOCTOR_DETAILS && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean isDuplicate(ArrayList<Doctor> doctors, Doctor newDoctor) {
        for (Doctor doc:doctors) {
            if (doc.getId() == newDoctor.getId())
                return true;
        }
        return false;
    }

    private void processNewDoctors(ArrayList<Doctor> newDoctors) {
        // Remove the end of list row
        if (recyclerViewItems.size() != 0 && recyclerViewItems.get(recyclerViewItems.size()-1).getItemType() == RecyclerViewItem.END_OF_LIST)
            recyclerViewItems.remove(recyclerViewItems.size()-1);

        Doctor doctor;
        for (int i=0; i<newDoctors.size(); i++) {
            doctor = newDoctors.get(i);
            // If appointment already exists skip it
            if (isDuplicate(doctors, doctor))
                continue;

            doctors.add(doctor);

            RecyclerViewItem item = new RecyclerViewItem();
            item.setDoctorType(doctor);
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
        if (recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void filterDoctors(ArrayList<Doctor> filteredDoctors) {
        doctors.clear();
        recyclerViewItems.clear();
        processNewDoctors(filteredDoctors);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
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

    @Override
    protected void backButton() {
        if (searchView.getVisibility() == View.VISIBLE) {
            hideSearchView();
            return;
        }

        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void showSearchView() {
        searchView.setVisibility(View.VISIBLE);
    }

    private void hideSearchView() {
        searchView.setVisibility(View.GONE);
        if (currentFilter != null)
            specialitySearch.setText(currentFilter);

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getRootView().getWindowToken(), 0);
    }
}