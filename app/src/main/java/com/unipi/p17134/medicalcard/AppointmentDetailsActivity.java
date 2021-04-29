package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.VerificationPopup;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Listeners.VerificationPopupListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

public class AppointmentDetailsActivity extends ConnectedBaseClass {
    private int id;
    private ImageView image;
    private TextView fullname, speciality, cost, date, address, phone, email;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.appointment_details_doctor_photo);
        fullname = findViewById(R.id.appointment_details_doctor_name);
        speciality = findViewById(R.id.appointment_details_doctor_speciality);
        cost = findViewById(R.id.appointment_details_doctor_cost);
        date = findViewById(R.id.appointment_details_doctor_date);
        address = findViewById(R.id.appointment_details_doctor_address);
        phone = findViewById(R.id.appointment_details_doctor_phone);
        email = findViewById(R.id.appointment_details_doctor_email);
        cancelButton = findViewById(R.id.cancel_appointment_button);
        cancelButton.setEnabled(false);

        id = getIntent().getIntExtra("id", 0);
        PatientDAO.appointment(this, id, new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                Appointment appointment = (Appointment)object;
                loadAppointmentData(appointment);
            }

            @Override
            public <T> void onErrorResponse(T error) {

            }
        });
    }

    private void loadAppointmentData(Appointment appointment) {
        if (appointment.getDoctor().getImage() != null)
            image.setImageBitmap(appointment.getDoctor().getImage());

        fullname.setText(appointment.getDoctor().getUser().getFullname());
        speciality.setText(appointment.getDoctor().getSpeciality());
        cost.setText(appointment.getDoctor().getCost()+" €");
        String dateTime = DateTimeParsing.dateToDateString(appointment.getStartDate()) + "  " + DateTimeParsing.dateToTimeString(appointment.getStartDate()) + "-" + DateTimeParsing.dateToTimeString(appointment.getEndDate());
        date.setText(dateTime);
        address.setText(appointment.getDoctor().getOfficeAddress());
        phone.setText(appointment.getDoctor().getPhone());
        email.setText(appointment.getDoctor().getEmail());

        cancelButton.setEnabled(true);
    }

    public void cancelAppointment(View view) {
        String appointment = "\n\n" + fullname.getText().toString() + "\n" + speciality.getText() + "\n" + date.getText().toString();

        Activity activity = this;
        VerificationPopup.showPopup(
                this,
                getResources().getString(R.string.cancel_appointment_popup_title_1),
                getResources().getString(R.string.cancel_appointment_popup_message_1) + appointment,
                getResources().getString(R.string.popup_positive_yes),
                getResources().getString(R.string.popup_negative_no),
                new VerificationPopupListener() {
                    @Override
                    public void onPositive() {
                        VerificationPopup.showPopup(
                                activity,
                                getResources().getString(R.string.cancel_appointment_popup_title_2),
                                getResources().getString(R.string.cancel_appointment_popup_message_2),
                                getResources().getString(R.string.popup_positive_yes),
                                getResources().getString(R.string.popup_negative_no),
                                new VerificationPopupListener() {
                                    @Override
                                    public void onPositive() {
                                        completeCancellation();
                                    }

                                    @Override
                                    public void onNegative() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel_appointment_failure), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onNegative() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel_appointment_failure), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void completeCancellation() {
        PatientDAO.deleteAppointment(this, id, new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                deletionComplete();
            }

            @Override
            public <T> void onErrorResponse(T error) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel_appointment_failure), Toast.LENGTH_SHORT).show();
            }
        });
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
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void deletionComplete() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel_appointment_success), Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        finish();
    }
}