package com.unipi.p17134.medicalcard.Singletons;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    private int id;
    private User user;

    public Patient(int id, User user) {
        this.id = id;
        this.user = user;
    }

    protected Patient(Parcel in) {
        id = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(user, flags);
    }
}
