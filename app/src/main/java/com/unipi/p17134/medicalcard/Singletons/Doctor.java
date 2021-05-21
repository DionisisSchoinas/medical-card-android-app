package com.unipi.p17134.medicalcard.Singletons;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.unipi.p17134.medicalcard.Custom.BitmapConversion;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Doctor implements Parcelable {
    private int id;
    private User user;
    private String speciality;
    private String office_address;
    private String phone;
    private String email;
    private float cost;
    private Bitmap image;

    public Doctor() {
        this.user = null;
        this.speciality = null;
        this.office_address = null;
        this.phone = null;
        this.email = null;
        this.cost = 0;
        this.image = null;
    }

    public Doctor(String speciality, String office_address, String phone, String email, float cost, Bitmap image) {
        this.user = null;
        this.speciality = speciality;
        this.office_address = office_address;
        this.phone = phone;
        this.email = email;
        this.cost = cost;
        this.image = image;
    }

    public Doctor(Doctor doctor) {
        this.id = doctor.id;
        this.user = doctor.user;
        this.speciality = doctor.speciality;
        this.office_address = doctor.office_address;
        this.phone = doctor.phone;
        this.email = doctor.email;
        this.cost = doctor.cost;
        this.image = doctor.image;
    }

    protected Doctor(Parcel in) {
        id = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        speciality = in.readString();
        office_address = in.readString();
        phone = in.readString();
        email = in.readString();
        cost = in.readFloat();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

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

    public Doctor setId(int id) {
        this.id = id;
        return this;
    }

    public String getSpeciality() {
        return speciality;
    }

    public Doctor setSpeciality(String speciality) {
        this.speciality = speciality;
        return this;
    }

    public String getOfficeAddress() {
        return office_address;
    }

    public Doctor setOfficeAddress(String office_address) {
        this.office_address = office_address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Doctor setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Doctor setEmail(String email) {
        this.email = email;
        return this;
    }

    public float getCost() {
        return cost;
    }

    public Doctor setCost(float cost) {
        this.cost = cost;
        return this;
    }

    public Bitmap getImage() {
        return image;
    }

    public Doctor setImage(Bitmap image) {
        this.image = image;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Doctor setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(user, flags);
        dest.writeString(speciality);
        dest.writeString(office_address);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeFloat(cost);
        dest.writeParcelable(image, flags);
    }
}
