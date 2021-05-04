package com.unipi.p17134.medicalcard.Singletons;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment implements Parcelable {
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

    protected Appointment(Parcel in) {
        id = in.readInt();
        doctor = in.readParcelable(Doctor.class.getClassLoader());
        patient = in.readParcelable(Patient.class.getClassLoader());
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(doctor, flags);
        dest.writeParcelable(patient, flags);
        if (startDate != null)
            dest.writeLong(startDate.getTime());
        else
            dest.writeLong(0);
        if (endDate != null)
            dest.writeLong(endDate.getTime());
        else
            dest.writeLong(0);
    }
}
