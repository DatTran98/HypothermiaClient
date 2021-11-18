package com.hust.temp.sevicesretrofit;

import com.hust.temp.Common.Constant;
import com.hust.temp.entities.Student;
import com.hust.temp.entities.StudentInfo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetListStudentsAPI {
    @GET(Constant.URL_LIST_STUDENTS)
    Call<List<StudentInfo>> getStudents ();
    @FormUrlEncoded
    @POST(Constant.URL_ADD_STUDENT)
    Call<String> addNewStudent(@Field("student_id") String studentId, @Field("student_name")String studentName, @Field("class_name")String className, @Field("birthday")String birthday);
}
