package com.example.btl.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Exam implements Parcelable {

    String id, name, status, password;
    long timeLimit;
    ArrayList<Question> questions;

    public Exam() {
    }

    public Exam(String id, String name, String status, String password, long timeLimit) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.password = password;
        this.timeLimit = timeLimit;
    }

    public Exam(String id, String name, String status, String password, long timeLimit, ArrayList<Question> questions) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.password = password;
        this.timeLimit = timeLimit;
        this.questions = questions;
    }

    protected Exam(Parcel in) {
        id = in.readString();
        name = in.readString();
        status = in.readString();
        password = in.readString();
        timeLimit = in.readInt();
        questions = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<Exam> CREATOR = new Creator<Exam>() {
        @Override
        public Exam createFromParcel(Parcel in) {
            return new Exam(in);
        }

        @Override
        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getStatus() {
        return status;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public long getTimeLimit() {
        return timeLimit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }
    public void shuffleQuestions() {
        Collections.shuffle(this.questions);
    }

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", password='" + password + '\'' +
                ", timeLimit=" + timeLimit +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(status);
        parcel.writeString(password);
        parcel.writeLong(timeLimit);
        parcel.writeTypedList(questions);
    }
}
