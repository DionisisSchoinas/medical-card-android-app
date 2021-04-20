package com.unipi.p17134.medicalcard.Singletons;

import android.graphics.Bitmap;

import com.unipi.p17134.medicalcard.Custom.BitmapConversion;

import org.json.JSONException;
import org.json.JSONObject;

public class Doctor {
    private int id;
    private String speciality;
    private String office_address;
    private String phone;
    private String email;
    private float cost;
    private Bitmap image;

    public Doctor(String speciality, String office_address, String phone, String email, float cost, Bitmap image) {
        this.speciality = speciality;
        this.office_address = office_address;
        this.phone = phone;
        this.email = email;
        this.cost = cost;
        this.image = image;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            if (speciality != null)
                json.put("speciality", speciality);
            if (office_address != null)
                json.put("office_address", office_address);
            if (phone != null)
                json.put("phone", phone);
            if (email != null)
                json.put("email", email);
            if (cost != 0)
                json.put("cost", cost);
            if (image != null)
                json.put("image_base64", BitmapConversion.bitmapToBase64(image));

            return json;
        }
        catch (JSONException e) {
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getOfficeAddress() {
        return office_address;
    }

    public void setOfficeAddress(String office_address) {
        this.office_address = office_address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
