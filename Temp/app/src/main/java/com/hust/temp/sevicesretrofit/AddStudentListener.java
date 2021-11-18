package com.hust.temp.sevicesretrofit;

import com.hust.temp.entities.Student;
import com.hust.temp.entities.StudentInfo;

import java.util.List;

public interface AddStudentListener {
    void getDataSuccess(String s);
    void getMessageError(Exception e);

}
