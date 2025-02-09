package com.example.btl.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SV implements Parcelable {
    private String msv, name, email;
    private ArrayList<Result> results;

    public SV() {
    }

    public SV(String msv, String name, String email) {
        this.msv = msv;
        this.name = name;
        this.email = email;
    }

    public SV(String msv, String name, String email, ArrayList<Result> results) {
        this.msv = msv;
        this.name = name;
        this.email = email;
        this.results = results;
    }

    protected SV(Parcel in) {
        msv = in.readString();
        name = in.readString();
        email = in.readString();
        results = in.createTypedArrayList(Result.CREATOR);
    }

    public static final Creator<SV> CREATOR = new Creator<SV>() {
        @Override
        public SV createFromParcel(Parcel in) {
            return new SV(in);
        }

        @Override
        public SV[] newArray(int size) {
            return new SV[size];
        }
    };

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "SV{" +
                "msv='" + msv + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(msv);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeTypedList(results);
    }
}
