package com.unipi.p17134.medicalcard.Singletons;

public class LoginResponse {
    private String authToken;
    private boolean isDoctor;
    private int doctorId;
    private User user;

    public LoginResponse(String authToken, boolean isDoctor, int doctorId, User user) {
        this.authToken = authToken;
        this.isDoctor = isDoctor;
        this.doctorId = doctorId;
        this.user = user;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public User getUser() {
        return user;
    }
}
