package com.hust.temp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hust.temp.Common.Constant;
import com.hust.temp.sevicesretrofit.GetDataListener;
import com.hust.temp.entities.StudentInfo;
import com.hust.temp.sevicesretrofit.GetListStudentsApiIml;
import com.hust.temp.ui.main.SectionsPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
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
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // Open Stream to write file.
            File dir = new File (root.getAbsolutePath());
            dir.mkdirs();
            File file = new File(dir, Constant.FILE_NAME);
            FileOutputStream out = new FileOutputStream(file);
            out.write(data.toString().getBytes(StandardCharsets.UTF_8));
            out.close();
            Toast.makeText(this, getResources().getString(R.string.export_success)+dir, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.export_failed)+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}