package com.unipi.p17134.medicalcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.unipi.p17134.medicalcard.API.PatientDAO;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.Popup.VerificationPopup;
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
        getSupportActionBar().setTitle(getResources().getString(R.string.appointment_details_activity));

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
        loadingDialog.startLoadingDialog();
        PatientDAO.appointment(this, id, new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                Appointment appointment = (Appointment)object;
                loadAppointmentData(appointment);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointmentData(Appointment appointment) {
        if (appointment.getDoctor().getImage() != null)
            image.setImageBitmap(appointment.getDoctor().getImage());

        fullname.setText(appointment.getDoctor().getUser().getFullname());
        speciality.setText(appointment.getDoctor().getSpeciality());
        cost.setText(appointment.getDoctor().getCost()+" ???");
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
                R.string.cancel_appointment_popup_title_1,
                getResources().getString(R.string.cancel_appointment_popup_message_1) + appointment,
                R.string.popup_positive_yes,
                R.string.popup_negative_no,
                new VerificationPopupListener() {
                    @Override
                    public void onPositive() {
                        VerificationPopup.showPopup(
                                activity,
                                R.string.cancel_appointment_popup_title_2,
                                R.string.cancel_appointment_popup_message_2,
                                R.string.popup_positive_yes,
                                R.string.popup_negative_no,
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
        loadingDialog.startLoadingDialog();
        PatientDAO.deleteAppointment(this, id, new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                deletionComplete();
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel_appointment_failure), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void backButton() {
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