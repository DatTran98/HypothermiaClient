package com.hust.temp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class StudentInfo implements Serializable {
    @SerializedName("studentId")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("studentName")
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @SerializedName("studentClass")
    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    @SerializedName("birthday")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @SerializedName("hypothermia")
    public double getHypothermia() {
        return hypothermia;
    }

    public void setHypothermia(double hypothermia) {
        this.hypothermia = hypothermia;
    }

    @SerializedName("lastUpdateDate")
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public StudentInfo() {

    }

    public StudentInfo(int id, String studentName, String studentClass, String birthday,
                       double hypothermia, Date lastUpdatedDate) {
        this.id = id;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.birthday = birthday;
        this.hypothermia = hypothermia;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    private int id;
    private String studentName, studentClass, birthday;
    private double hypothermia;
    private Date lastUpdatedDate;
}
