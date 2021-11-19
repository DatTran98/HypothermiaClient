package com.hust.temp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
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
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hust.temp.Common.Constant;
import com.hust.temp.entities.StudentInfo;
import com.hust.temp.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab, btnExport;
    private TextView txtTitle;
    private ArrayList<StudentInfo> listStudentInfoSource = new ArrayList<>();
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
                getListExport();
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
            ArrayList<StudentInfo> listUserInfoExport = listStudentInfoSource;
            data.append(Constant.FILE_HEADER);
            data.append(Constant.NEW_LINE_SEPARATOR);
            for (StudentInfo studentInfo : listUserInfoExport) {
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
            }
            File root =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // Open Stream to write file.
            File dir = new File(root.getAbsolutePath());
            dir.mkdirs();
            File file = new File(dir, Constant.FILE_NAME);
            FileOutputStream out = new FileOutputStream(file);
            out.write(data.toString().getBytes(StandardCharsets.UTF_8));
            out.close();
            Toast.makeText(this, getResources().getString(R.string.export_success) + dir,
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this,
                    getResources().getString(R.string.export_failed) + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getListExport() {
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.ROOT_URL_SUB1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj != null) {
                        JSONArray jsonArrayRoom = jsonObj.getJSONArray(Constant.DATA_INFO);
                        for (int i = 0; i < jsonArrayRoom.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArrayRoom.get(i);
                            Date date;
                            double tempValue;
                            int id;
                            if (!obj.getString(Constant.KEY_HYPOTHERMIA_LAST_UPDATE).equals(
                                    "null")) {
                                date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.getString(Constant.KEY_HYPOTHERMIA_LAST_UPDATE));
                            } else {
                                date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-01-01 12:00:00");
                            }
                            if (!(obj.getString(Constant.KEY_HYPOTHERMIA_VALUE).equals("null"))) {
                                tempValue =
                                        Double.parseDouble(obj.getString(Constant.KEY_HYPOTHERMIA_VALUE));
                            } else {
                                tempValue = 0;
                            }
                            if (!(obj.getString(Constant.KEY_STUDENT_ID).equals("null"))) {
                                id = Integer.parseInt(obj.getString(Constant.KEY_STUDENT_ID));
                            } else {
                                id = 0;
                            }

                            StudentInfo studentInfo = new StudentInfo(id,
                                    obj.getString(Constant.KEY_STUDENT_NAME),
                                    obj.getString(Constant.KEY_STUDENT_CLASS),
                                    obj.getString(Constant.KEY_STUDENT_BIRTHDAY), tempValue,
                                    date);
                            listStudentInfoSource.add(studentInfo);
                            exportCSV();
                        }
                    }
                } catch (JSONException | ParseException e) {
                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            getResources().getString(R.string.can_trans_data),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
        loading = ProgressDialog.show(this,
                "",
                getResources().getString(R.string.export), false, false);
    }
}