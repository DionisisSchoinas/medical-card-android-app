package com.unipi.p17134.medicalcard.Singletons;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Appointment {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    private int id;
    private Doctor doctor;
    private Patient patient;
    private Date startDate;
    private Date endDate;

    public Appointment() {
        this.id = 0;
        this.doctor = null;
        this.patient = null;
        this.startDate = null;
        this.endDate = null;
    }

    public Appointment(int id, Doctor doctor, Patient patient, Date startDate, Date endDate) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
