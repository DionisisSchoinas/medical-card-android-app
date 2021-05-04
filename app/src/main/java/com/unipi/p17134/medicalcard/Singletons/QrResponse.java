package com.unipi.p17134.medicalcard.Singletons;

public class QrResponse {
    private Appointment currentAppointment;
    private Appointment previousAppointment;
    private Patient patient;

    public QrResponse() {
        this.currentAppointment = null;
        this.previousAppointment = null;
        this.patient = null;
    }

    public QrResponse(Appointment currentAppointment, Appointment previousAppointment, Patient patient) {
        this.currentAppointment = currentAppointment;
        this.previousAppointment = previousAppointment;
        this.patient = patient;
    }

    public Appointment getCurrentAppointment() {
        return currentAppointment;
    }

    public void setCurrentAppointment(Appointment currentAppointment) {
        this.currentAppointment = currentAppointment;
    }

    public Appointment getPreviousAppointment() {
        return previousAppointment;
    }

    public void setPreviousAppointment(Appointment previousAppointment) {
        this.previousAppointment = previousAppointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
