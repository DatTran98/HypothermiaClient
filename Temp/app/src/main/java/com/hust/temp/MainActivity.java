package com.hust.temp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hust.temp.Common.Constant;
import com.hust.temp.entities.Student;
import com.hust.temp.sevicesretrofit.GetDataListener;
import com.hust.temp.entities.StudentInfo;
import com.hust.temp.sevicesretrofit.GetListStudentsApiIml;
import com.hust.temp.ui.main.SectionsPagerAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton fab,btnExport;
    private TextView txtTitle;
    private ArrayList<StudentInfo> listStudentInfoSource = new ArrayList<>();
    private GetListStudentsApiIml getListStudentsApiIml;
    private Boolean exit = false;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());
        findViewById();
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        txtTitle.setText(R.string.temp_result);
                        break;
                    case 1:
                        txtTitle.setText(R.string.list_students);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddStudentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCSV();
            }
        });

//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                // To keep animation for 4 seconds
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Stop animation (This will be after 3 seconds)
//                        swipeContainer.setRefreshing(false);
//                    }
//                }, 4000); //
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }

    private void getData() {
        loading = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading_data), getResources().getString(R.string.waiting_minute), false, false);
        getListStudentsApiIml = new GetListStudentsApiIml();
        getListStudentsApiIml.getDataListStudents(new GetDataListener() {
            @Override
            public void getDataSuccess(List<StudentInfo> studentInfoList) {
                listStudentInfoSource = (ArrayList<StudentInfo>) studentInfoList;
                getIntent().putExtra(Constant.DATA_INFO, listStudentInfoSource);
                loading.dismiss();
                Toast.makeText(MainActivity.this, "getResources().getString(R.string.server_error)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void getMessageError(Exception e) {
                listStudentInfoSource = new ArrayList<>();
                getIntent().putExtra(Constant.DATA_INFO, listStudentInfoSource);
                loading.dismiss();
                Toast.makeText(MainActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findViewById() {
        txtTitle = findViewById(R.id.title);
        fab = findViewById(R.id.fab);
        btnExport = findViewById(R.id.btn_export);
//        swipeContainer = findViewById(R.id.swipeContainer);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.click_back_again),
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportCSV() {
        StringBuilder data = new StringBuilder();
        try {
            ArrayList<StudentInfo> listUserInfoExport = new ArrayList<>();
            data.append(Constant.FILE_HEADER);
            data.append(Constant.NEW_LINE_SEPARATOR);
            int index = 0;
            for (StudentInfo studentInfo : listUserInfoExport) {
                data.append(index);
                data.append(Constant.COMMA_DELIMITER);
                data.append(studentInfo.getId());
                data.append(Constant.COMMA_DELIMITER);
                data.append(studentInfo.getStudentName());
                data.append(Constant.COMMA_DELIMITER);
                data.append(studentInfo.getStudentClass());
                data.append(Constant.COMMA_DELIMITER);
                data.append(studentInfo.getBirthday());
                data.append(Constant.COMMA_DELIMITER);
                data.append(studentInfo.getHypothermia());
                data.append(Constant.COMMA_DELIMITER);
                data.append(studentInfo.getLastUpdatedDate());
                data.append(Constant.NEW_LINE_SEPARATOR);
                index++;
            }
            // Open Stream to write file.
            FileOutputStream out = this.openFileOutput("simpleFileName.csv", MODE_PRIVATE);
            // Write.
            out.write(data.toString().getBytes(StandardCharsets.UTF_8));
            out.close();
            Toast.makeText(this, "OKKK", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "NOOO" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    class GetListStudent extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            exportCSV();
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loading_data), getResources().getString(R.string.export), false, false);
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            loading.dismiss();
        }
    }
}