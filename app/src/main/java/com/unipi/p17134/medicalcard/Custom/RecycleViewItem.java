package com.unipi.p17134.medicalcard.Custom;

import com.unipi.p17134.medicalcard.Singletons.Appointment;

public class RecycleViewItem {
    public static final int DATE_SPLITTER = 1;
    public static final int PATIENT_APPOINTMENT = 2;

    private int itemType = 0;
    private String dateString;
    private Appointment appointment;

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

    public String getDateString() {
        return dateString;
    }

    public Appointment getAppointmentData() {
        return appointment;
    }
}
