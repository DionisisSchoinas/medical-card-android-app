package com.unipi.p17134.medicalcard.Singletons;

public class LoginResponse {
    private String authToken;
    private boolean isDoctor;
    private User user;

    public LoginResponse(String authToken, boolean isDoctor, User user) {
        this.authToken = authToken;
        this.isDoctor = isDoctor;
        this.user = user;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    public User getUser() {
        return user;
    }
}
