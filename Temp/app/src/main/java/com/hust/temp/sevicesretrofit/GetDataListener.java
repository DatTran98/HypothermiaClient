package com.hust.temp.sevicesretrofit;

import com.hust.temp.entities.StudentInfo;

import java.util.List;

public interface GetDataListener {
    void getDataSuccess(List<StudentInfo> account);
    void getMessageError(Exception e);
}
