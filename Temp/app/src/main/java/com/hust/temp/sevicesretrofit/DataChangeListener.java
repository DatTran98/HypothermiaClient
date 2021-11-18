package com.hust.temp.sevicesretrofit;

import com.hust.temp.entities.StudentInfo;

import java.util.List;

public interface DataChangeListener {
    void listenDataChange(List<StudentInfo> account);
}
