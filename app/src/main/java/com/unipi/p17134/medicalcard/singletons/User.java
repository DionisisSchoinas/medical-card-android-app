package com.unipi.p17134.medicalcard.singletons;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String email;
    private String password;
    private String passwordConfirmation;
    private String amka;
    private String fullname;
    private String dateOfBirth;

    public User() {
        this.email = "";
        this.password = "";
        this.passwordConfirmation = "";
        this.amka = "";
        this.fullname = "";
        this.dateOfBirth = "";
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String amka, String email, String password, String passwordConfirmation, String fullname, String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.amka = amka;
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            if (email != null)
                json.put("email", email);
            if (password != null)
                json.put("password", password);
            if (passwordConfirmation != null)
                json.put("password_confirmation", passwordConfirmation);
            if (amka != null)
                json.put("amka", amka);
            if (fullname != null)
                json.put("fullname", fullname);
            if (dateOfBirth != null)
                json.put("date_of_birth", dateOfBirth);

            return json;
        }
        catch (JSONException e) {
            return null;
        }
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public User setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
        return this;
    }

    public String getAmka() {
        return amka;
    }

    public User setAmka(String amka) {
        this.amka = amka;
        return this;
    }

    public String getFullname() {
        return fullname;
    }

    public User setFullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public User setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }
}
