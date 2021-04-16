package com.unipi.p17134.medicalcard;

import android.os.Bundle;
import android.view.View;

import com.unipi.p17134.medicalcard.custom.API;

public class MainActivity extends ConnectedBaseClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logout(View view) {
        API.UserDAO.logout(this);
    }
}