package com.hust.temp.sevicesretrofit;

import android.util.Log;

import com.hust.temp.entities.Student;
import com.hust.temp.entities.StudentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetListStudentsApiIml extends BaseRetrofitIml {
    String TAG = GetListStudentsApiIml.class.getSimpleName();
    GetListStudentsAPI studentsAPI;
    Retrofit retrofit = getRetrofit();

    public void getDataListStudents(final GetDataListener listener) {
        studentsAPI = retrofit.create(GetListStudentsAPI.class);
        Call<List<StudentInfo>> call = studentsAPI.getStudents();
        call.enqueue(new Callback<List<StudentInfo>>() {
            @Override
            public void onResponse(Call<List<StudentInfo>> call,
                                   Response<List<StudentInfo>> response) {
                //Kết quả trả về dạng String nên cần chuyển về dạng Json
                if (response.isSuccessful()) {
//                    try {
                    ArrayList<StudentInfo> listStudentsInfo = new ArrayList<>();
//                        JSONObject jsonObject = new JSONObject(response.body().string());
//                        int status = jsonObject.getInt("status");
//                        if (status == 1) {
//                            JSONArray jsonArray = new JSONArray();
//                            StudentInfo account = new StudentInfo();
//                            account.setUserName(jsonObject.getString("user_name"));
//                            account.setEmail(jsonObject.getString("email"));

//                            listener.getDataSuccess(listStudentsInfo);
//                        } else {

//                            Log.d(TAG, "onResponse: "+jsonObject.toString());
//                        }
                    listStudentsInfo = (ArrayList<StudentInfo>) response.body();
                    listener.getDataSuccess(listStudentsInfo);

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }

            @Override
            public void onFailure(Call<List<StudentInfo>> call, Throwable t) {
                listener.getMessageError(new Exception(t));
            }
        });

    }

    public void addNewStudent(final AddStudentListener listener,String studentId, String studentName,
                              String className, String birthDay) {
        Log.d(TAG, "onRespobababaaaaaaaaanse: "+retrofit);
        studentsAPI = retrofit.create(GetListStudentsAPI.class);
        Call<String> call = studentsAPI.addNewStudent(studentId,studentName, className, birthDay);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {

                listener.getDataSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                listener.getMessageError(new Exception(t));
            }
        });
    }
}
