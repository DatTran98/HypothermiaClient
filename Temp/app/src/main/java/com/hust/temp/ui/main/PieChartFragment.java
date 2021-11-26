package com.hust.temp.ui.main;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hust.temp.Common.Common;
import com.hust.temp.Common.Constant;
import com.hust.temp.R;
import com.hust.temp.entities.StudentInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartFragment extends Fragment {
    private ArrayList<StudentInfo> listStudentInfoSource = new ArrayList<>();
    private PieChart pieChart;
    private TableLayout tblStudentNotYes, tblHypothermiaHeader;
    private NestedScrollView scrollView;
    private Button viewStudentNotYes;
    private boolean isViewStudent = false;

    public static PieChartFragment newInstance() {
        PieChartFragment fragment = new PieChartFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pie_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);
        viewStudentNotYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isViewStudent) {
                    scrollView.setVisibility(View.VISIBLE);
                    tblHypothermiaHeader.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.GONE);
                    isViewStudent = true;
                    viewStudentNotYes.setText(getResources().getString(R.string.see_chart));
                } else {
                    scrollView.setVisibility(View.GONE);
                    tblHypothermiaHeader.setVisibility(View.GONE);
                    pieChart.setVisibility(View.VISIBLE);
                    isViewStudent = false;
                    viewStudentNotYes.setText(getResources().getString(R.string.view_student_not_yes));
                }
            }
        });
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            listStudentInfoSource =
                    (ArrayList<StudentInfo>) bundle.get(Constant.KEY_LIST_HYPOTHERMIA);
            drawChart(listStudentInfoSource);
        }
    }

    private void drawTable(ArrayList<StudentInfo> listStudentInfo) {
        tblStudentNotYes.removeAllViews();
        if (listStudentInfo != null && !listStudentInfo.isEmpty()) {

            for (StudentInfo st : listStudentInfo) {
                TableRow tbrow = new TableRow(getContext());
                TextView t1v = new TextView(getContext());
                t1v.setText(st.getStudentName());
                setTypeForView(t1v, false);
                tbrow.addView(t1v);
                TextView t2v = new TextView(getContext());
                t2v.setText(st.getStudentClass());
                setTypeForView(t2v, false);
                tbrow.addView(t2v);
                TextView t3v = new TextView(getContext());
                t3v.setText(st.getBirthday());
                setTypeForView(t3v, false);
                tbrow.addView(t3v);
                TextView t4v = new TextView(getContext());
                t4v.setText(String.format("%s",st.getHypothermia()));
                setTypeForView(t4v, false);
                tbrow.addView(t4v);
                TextView t5v = new TextView(getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    t5v.setText(new SimpleDateFormat(Constant.FORMAT_PARTEN, Locale.ROOT).format(st.getLastUpdatedDate()));
                }
                setTypeForView(t5v, true);
                tbrow.addView(t5v);
                tblStudentNotYes.addView(tbrow);
            }
        } else {
            TableRow tblRow = new TableRow(getContext());
            TextView t0v = new TextView(getContext());
            t0v.setText(getResources().getString(R.string.no_data));
            tblRow.addView(t0v);
            setTypeForView(t0v, false);
            tblStudentNotYes.addView(tblRow);
        }
    }

    private void setTypeForView(TextView textView, boolean isDateView) {
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        if (isDateView) {
            layoutParams.weight = 5;
        } else {
            layoutParams.weight = 4;
        }
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        float scale = this.getResources().getDisplayMetrics().density;
        int padding = (int) (3 * scale + 0.5f);
        textView.setPadding(padding, padding, padding, padding);
        textView.setTextColor(Color.BLACK);
    }

    private void findViewById(View view) {
        pieChart = view.findViewById(R.id.pieChart);
        tblStudentNotYes = view.findViewById(R.id.tbl_student_not_yes);
        scrollView = view.findViewById(R.id.scroll_view);
        tblHypothermiaHeader = view.findViewById(R.id.tbl_hypothermia_header);
        viewStudentNotYes = view.findViewById(R.id.viewStudentNotYes);
    }

    private void drawChart(ArrayList<StudentInfo> listStudentInfoSource) {
        pieChart.setUsePercentValues(true);

        Map<Long, StudentInfo> map = new HashMap<Long, StudentInfo>();
        for (StudentInfo studentInfo : listStudentInfoSource) {
            long key = studentInfo.getId();
            if (!map.containsKey(key)) {
                map.put(key, studentInfo);
            }
        }
        Collection<StudentInfo> uniqueStudentInfoCollection = map.values();
        ArrayList<StudentInfo> uniqueStudentInfo = new ArrayList<>(uniqueStudentInfoCollection);
        ArrayList<StudentInfo> listStudentDone =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            listStudentDone = (ArrayList<StudentInfo>) uniqueStudentInfo.stream()
                    .filter(p -> p.getLastUpdatedDate().getTime() <= Common.getEndOfDay().getTime())
                    .filter(p -> p.getLastUpdatedDate().getTime() >= Common.getStartOfDay().getTime())
                    .filter(p -> p.getHypothermia() > 0).collect(Collectors.toList());
        }

        ArrayList<StudentInfo> listStudentNotYes = new ArrayList<>(uniqueStudentInfoCollection);;
        listStudentNotYes.removeAll(listStudentDone);
        float studentNotYesPercentage =
                ((float) listStudentNotYes.size() / (float) uniqueStudentInfo.size()) * 100;
        float studentDonePercentage = 100 - studentNotYesPercentage;

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(studentDonePercentage,
                getString(R.string.temp_done) + Constant.COMMA + listStudentDone.size() + Constant.COMMA_DIVISION + uniqueStudentInfo.size(), 0));
        yValues.add(new PieEntry(studentNotYesPercentage,
                getString(R.string.temp_not_yes) + Constant.COMMA + (uniqueStudentInfo.size() - listStudentDone.size()) + Constant.COMMA_DIVISION + uniqueStudentInfo.size(), 1));

        PieDataSet dataSet = new PieDataSet(yValues, getString(R.string.hypothermia_results));
        PieData data = new PieData(dataSet);

        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);

        Description description = new Description();
        description.setText(getString(R.string.pie_chart_title));
        pieChart.setDescription(description);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(20f);
        pieChart.setHoleRadius(20f);

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(0f);

        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        drawTable(listStudentNotYes);
    }
}
