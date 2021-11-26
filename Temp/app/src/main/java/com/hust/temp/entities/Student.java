package com.hust.temp.entities;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("student_id")
    public long studentID;
    @SerializedName("student_name")
    public String studentName;
    @SerializedName("class_name")
    public String studentClass;
    @SerializedName("birthday")
    public String birthday;

    public long getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Student(long studentID, String studentName, String studentClass, String birthday) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentID=" + studentID +
                ", studentName='" + studentName + '\'' +
                ", studentClass='" + studentClass + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}