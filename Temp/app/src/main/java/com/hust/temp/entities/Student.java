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

    public String getStudentName() {
        return studentName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getBirthday() {
        return birthday;
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