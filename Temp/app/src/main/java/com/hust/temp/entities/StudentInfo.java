package com.hust.temp.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class StudentInfo implements Serializable {
    @SerializedName("studentId")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SerializedName("studentName")
    public String getStudentName() {
        return studentName;
    }


    @SerializedName("studentClass")
    public String getStudentClass() {
        return studentClass;
    }


    @SerializedName("birthday")
    public String getBirthday() {
        return birthday;
    }


    @SerializedName("hypothermia")
    public double getHypothermia() {
        return hypothermia;
    }


    @SerializedName("lastUpdateDate")
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }


    public StudentInfo(long id, String studentName, String studentClass, String birthday,
                       double hypothermia, Date lastUpdatedDate) {
        this.id = id;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.birthday = birthday;
        this.hypothermia = hypothermia;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    private long id;
    private final String studentName;
    private final String studentClass;
    private final String birthday;
    private final double hypothermia;
    private final Date lastUpdatedDate;
}
