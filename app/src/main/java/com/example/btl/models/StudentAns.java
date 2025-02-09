package com.example.btl.models;

public class StudentAns {
    private String correctAns, studentAns;

    public StudentAns() {
    }

    public StudentAns(String correctAns, String studentAns) {
        this.correctAns = correctAns;
        this.studentAns = studentAns;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public String getStudentAns() {
        return studentAns;
    }

    public void setStudentAns(String studentAns) {
        this.studentAns = studentAns;
    }

    @Override
    public String toString() {
        return "StudentAns{" +
                "correctAns='" + correctAns + '\'' +
                ", studentAns='" + studentAns + '\'' +
                '}';
    }
}
