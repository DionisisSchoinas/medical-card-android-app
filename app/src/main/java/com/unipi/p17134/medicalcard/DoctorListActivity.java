package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Adapters.DoctorListAdapter;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.Popup.InputPopup;
import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Listeners.InputPopupListener;
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

    private String currentFilter;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        DoctorDAO.resetCounters();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.doctor_list_activity));

        Activity activity = this;

        responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                processNewDoctors((ArrayList<Doctor>)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        };

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
                loadingDialog.startLoadingDialog();
                DoctorDAO.doctors(activity, -2, currentFilter, responseListener);
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
                    DoctorDAO.doctors(activity, -1, currentFilter, responseListener);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (recyclerView.getAdapter() == null)
                    return;

                // If last visible item's index is greater than the total appointments - 10
                if (currentDisplayState == RecyclerView.SCROLL_STATE_DRAGGING && dy > 0 && layoutManager.findLastVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - 10) {
                    DoctorDAO.doctors(activity, -1, currentFilter, responseListener);
                }
            }
        });

        processNewDoctors(new ArrayList<>());
        loadingDialog.startLoadingDialog();
        // Fill with 1 page of doctors
        DoctorDAO.doctors(activity, 1, currentFilter, responseListener);
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return;
        }

        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void searchWithFilter(String filter) {
        DAOResponseListener filterResponseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                filterDoctors((ArrayList<Doctor>)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;
                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        };

        loadingDialog.startLoadingDialog();
        DoctorDAO.resetCounters();
        DoctorDAO.doctors(this, 1, filter, filterResponseListener);
    }

    private void showSearchView() {
        dialog = InputPopup.showPopup(this, R.string.filter_doctors, currentFilter, R.string.popup_positive_search, R.string.popup_negative_cancel, new InputPopupListener() {
            @Override
            public void onPositive(String value) {
                searchWithFilter(value);
                currentFilter = value;
            }

            @Override
            public void onNegative() {

            }
        });
    }
}