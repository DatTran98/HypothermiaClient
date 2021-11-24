package com.hust.temp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.hust.temp.Common.Constant;
import com.hust.temp.entities.StudentInfo;
import com.hust.temp.ui.main.SectionsPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


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
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
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
                    case 2:
                        txtTitle.setText(R.string.pie_chart_title);
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

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), AddStudentActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        });
        btnExport.setOnClickListener(v -> getListExport());

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
            new Handler().postDelayed(() -> exit = false, 3 * 1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void exportCSV() {
        StringBuilder data = new StringBuilder();
        try {
            ArrayList<StudentInfo> listUserInfoExport = listStudentInfoSource;
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                File root =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                // Open Stream to write file.
                File dir = new File(root.getAbsolutePath());
//                dir.mkdirs();
//                File file = new File(dir, Constant.FILE_NAME);
//                FileOutputStream out = new FileOutputStream(file);
//                out.write(data.toString().getBytes(StandardCharsets.UTF_8));
//                out.close();
                Workbook workbook = new Workbook();
                for (int i = 0; i < Constant.ARRAY_HEADER.length; i++) {
                    workbook.getWorksheets().get(0).getCells().get(0, i).putValue(Constant.ARRAY_HEADER[i]);
                }
                workbook.getWorksheets().get(0).setName(Constant.STUDENT_TEMP);
                Cells cells = workbook.getWorksheets().get(0).getCells();
                for (int i = 0; i < listUserInfoExport.size(); i++) {
                    cells.get(i + 1, 0).putValue(listUserInfoExport.get(i).getId());
                    cells.get(i + 1, 1).putValue(listUserInfoExport.get(i).getStudentName());
                    cells.get(i + 1, 2).putValue(listUserInfoExport.get(i).getStudentClass());
                    cells.get(i + 1, 3).putValue(listUserInfoExport.get(i).getBirthday());
                    cells.get(i + 1, 4).putValue(listUserInfoExport.get(i).getHypothermia());
                    cells.get(i + 1, 5).putValue((new SimpleDateFormat(Constant.FORMAT_PARTEN,
                            Locale.ROOT).format(listUserInfoExport.get(i).getLastUpdatedDate())));
                }
                workbook.save(dir + Constant.FILE_NAME);
                Toast.makeText(this, getResources().getString(R.string.export_success) + dir,
                        Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this,
                    getResources().getString(R.string.export_failed) + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getListExport() {
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.ROOT_URL_SUB1, response -> {
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONArray jsonArrayRoom = jsonObj.getJSONArray(Constant.DATA_INFO);
                for (int i = 0; i < jsonArrayRoom.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArrayRoom.get(i);
                    Date date;
                    double tempValue;
                    int id;
                    if (!obj.getString(Constant.KEY_HYPOTHERMIA_LAST_UPDATE).equals(
                            Constant.NULL_VALUE)) {
                        date = new java.text.SimpleDateFormat(Constant.FORMAT_PARTEN,
                                Locale.ROOT).parse(obj.getString(Constant.KEY_HYPOTHERMIA_LAST_UPDATE));
                    } else {
                        date = new java.text.SimpleDateFormat(Constant.FORMAT_PARTEN,
                                Locale.ROOT).parse("2021-01-01 12:00:00");
                    }
                    if (!(obj.getString(Constant.KEY_HYPOTHERMIA_VALUE).equals(Constant.NULL_VALUE))) {
                        tempValue =
                                Double.parseDouble(obj.getString(Constant.KEY_HYPOTHERMIA_VALUE));
                    } else {
                        tempValue = 0;
                    }
                    if (!(obj.getString(Constant.KEY_STUDENT_ID).equals(Constant.NULL_VALUE))) {
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
                    loading.dismiss();
                }
            } catch (JSONException | ParseException e) {
                loading.dismiss();
                Toast.makeText(MainActivity.this.getApplicationContext(),
                        getResources().getString(R.string.can_trans_data),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }, error -> {
            loading.dismiss();
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
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