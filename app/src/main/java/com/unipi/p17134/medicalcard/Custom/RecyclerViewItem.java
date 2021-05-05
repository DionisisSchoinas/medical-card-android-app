package com.unipi.p17134.medicalcard.Custom;

import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

public class RecyclerViewItem {
    public static final int DATE_SPLITTER = 1;
    public static final int PATIENT_APPOINTMENT = 2;
    public static final int DOCTOR_SCHEDULE_ITEM = 3;
    public static final int DOCTOR_APPOINTMENT = 4;
    public static final int END_OF_LIST = 5;
    public static final int DOCTOR = 6;

    private int itemType = 0;
    private String dateString;
    private Appointment appointment;
    private Doctor doctor;
    private boolean booked = false;

    public int getItemType() {
        return itemType;
    }

    public void setDateSplitterType(String dateString) {
        this.itemType = DATE_SPLITTER;
        this.dateString = dateString;
    }

    public void setPatientAppointmentType(Appointment appointment) {
        this.itemType = PATIENT_APPOINTMENT;
        this.appointment = appointment;
    }

    public void setDoctorScheduleItem(Appointment appointment, boolean booked) {
        this.itemType = DOCTOR_SCHEDULE_ITEM;
        this.appointment = appointment;
        this.booked = booked;
    }

    public void setDoctorAppointmentType(Appointment appointment) {
        this.itemType = DOCTOR_APPOINTMENT;
        this.appointment = appointment;
    }

    public void setEndOfListType() {
        this.itemType = END_OF_LIST;
    }

    public void setDoctorType(Doctor doctor) {
        this.itemType = DOCTOR;
        this.doctor = doctor;
    }

    public String getDateString() {
        return dateString;
    }

    public Appointment getAppointmentData() {
        return appointment;
    }

    public Doctor getDoctorData() {
        return doctor;
    }

    public boolean isBooked() {
        return booked;
    }

    public void isBooked(boolean booked) {
        this.booked = booked;
    }
}
