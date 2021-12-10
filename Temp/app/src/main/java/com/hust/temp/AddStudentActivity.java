package com.hust.temp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textview.MaterialTextView;
import com.hust.temp.Common.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class AddStudentActivity extends AppCompatActivity {
    private EditText studentId, studentName, studentClass, birthDay;
    private CircularProgressButton btnSave;
    private ImageView btnBack;
    private ImageButton btnPickDate;
    private MaterialTextView message;
    private ProgressDialog loading;
    SharedPreferences.Editor preferencesEditor;
    SharedPreferences mPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);
        mPreferences = getSharedPreferences(Constant.SHARED_PROFILE, MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();
        String isSignedIn = mPreferences.getString(Constant.IS_LOGGED_IN, Constant.FALSE);
        if (isSignedIn.equals(Constant.FALSE)) {
            Intent i = new Intent(AddStudentActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        findViewById();
        setEvent();
    }

    private void setEvent() {
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        });
        btnSave.setOnClickListener(v -> {

            String id = studentId.getText().toString();
            String name = studentName.getText().toString();
            String stClass = studentClass.getText().toString();
            String birthday = birthDay.getText().toString();

            boolean validate = validateField(id, name, stClass, birthday);
            if (!validate) {
                message.setText(getResources().getString(R.string.message_fill_all_text));
                message.setVisibility(View.VISIBLE);
            } else {
                message.setVisibility(View.GONE);
                loading = ProgressDialog.show(AddStudentActivity.this, getResources().getString(R.string.adding_new_student),
                        getResources().getString(R.string.waiting_minute), false, false);
                addNewStudentVoleley(id, name, stClass, birthday);
            }
        });
        btnPickDate.setOnClickListener(view -> buttonSelectDate());
    }

    private void addNewStudentVoleley(String id, String name, String stClass, String birthday) {

        String url = Constant.ROOT_URL_ADD;

        RequestQueue queue = Volley.newRequestQueue(AddStudentActivity.this);

        StringRequest requestString = new StringRequest(Request.Method.POST, url,
                response -> {
                    loading.dismiss();
                    try {
                        // on below line we are passing our response
                        // to json object to extract data from it.
                        JSONObject respObj = new JSONObject(response);
                        int status = respObj.getInt(Constant.STATUS);
                        if (status == 1) {
                            setDefaultValue();
                            message.setText(getResources().getString(R.string.add_student_success));
                            message.setTextColor(getResources().getColor(R.color.teal_200));
                            message.setVisibility(View.VISIBLE);
                            setDefaultValue();
                        } else if (status == 2) {
                            message.setText(getResources().getString(R.string.exist_student));
                            message.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(AddStudentActivity.this,
                                    getResources().getString(R.string
                                    .add_student_failed), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddStudentActivity.this,
                                getResources().getString(R.string
                                .can_trans_data), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    loading.dismiss();
                    // method to handle errors.
                    Toast.makeText(AddStudentActivity.this,
                            getResources().getString(R.string.server_error) + error,
                            Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(Constant.KEY_STUDENT_ID, id);
                params.put(Constant.KEY_STUDENT_NAME, name);
                params.put(Constant.KEY_STUDENT_CLASS, stClass);
                params.put(Constant.KEY_STUDENT_BIRTHDAY, birthday);

                return params;
            }
        };
        // below line is to make
        // a json object request.
        requestString.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestString.setShouldCache(false);
        queue.add(requestString);
    }

    private void setDefaultValue() {
        studentId.setText("");
        studentName.setText("");
        studentClass.setText("");
        birthDay.setText("");
    }

    private boolean validateField(String id, String name, String stClass, String birthday) {
        return !id.isEmpty() && !name.isEmpty() && !stClass.isEmpty() && !birthday.isEmpty();
    }

    private void findViewById() {
        studentId = findViewById(R.id.editTextId);
        studentName = findViewById(R.id.editTextName);
        studentClass = findViewById(R.id.editTextClass);
        birthDay = findViewById(R.id.editTextBirthDay);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnPickDate = findViewById(R.id.btn_pick_date);
        message = findViewById(R.id.message);
    }

    private void buttonSelectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddStudentActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> birthDay.setText(year1 + "-" + (monthOfYear + 1) + "-" +dayOfMonth  ), year, month, day);
        datePickerDialog.show();
    }
}