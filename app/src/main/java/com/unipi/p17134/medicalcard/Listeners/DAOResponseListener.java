package com.unipi.p17134.medicalcard.Listeners;

public interface DAOResponseListener {
    <T> void onResponse(T object);
    <T> void onErrorResponse(T error);
}
