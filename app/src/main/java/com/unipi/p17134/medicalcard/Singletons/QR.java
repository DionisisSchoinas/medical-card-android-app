package com.unipi.p17134.medicalcard.Singletons;

public class QR {
    private String token;
    private int expiresAfterSeconds;

    public QR(String token, int expiresAfterSeconds) {
        this.token = token;
        this.expiresAfterSeconds = expiresAfterSeconds;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpiresAfterSeconds() {
        return expiresAfterSeconds;
    }

    public void setExpiresAfterSeconds(int expiresAfterSeconds) {
        this.expiresAfterSeconds = expiresAfterSeconds;
    }
}
