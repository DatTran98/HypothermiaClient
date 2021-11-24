package com.hust.temp.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hust.temp.Common.Constant;
import com.hust.temp.R;
import com.hust.temp.entities.Student;
import com.hust.temp.entities.StudentInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartFragment extends Fragment {
    private ArrayList<StudentInfo> listStudentInfoSource = new ArrayList<>();
    private PieChart pieChart;

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
    }

    private void findViewById(View view) {
        pieChart = view.findViewById(R.id.pieChart);
        drawChart();
    }

    private void drawChart() {
        pieChart.setUsePercentValues(true);
        listStudentInfoSource =
                (ArrayList<StudentInfo>) getActivity().getIntent().getExtras().get(Constant.KEY_LIST_HYPOTHERMIA);

        Map<Integer, StudentInfo> map = new HashMap<Integer, StudentInfo>();
        for (StudentInfo studentInfo : listStudentInfoSource) {
            int key = studentInfo.getId();
            if (!map.containsKey(key)) {
                map.put(key, studentInfo);
            }
        }
        Collection<StudentInfo> uniqueStudentInfoCollection = map.values();

        ArrayList<StudentInfo> uniqueStudentInfo = new ArrayList<>(uniqueStudentInfoCollection);

//        HashSet<StudentInfo> hashSet = new HashSet(listStudentInfoSource);
//        ArrayList<StudentInfo> listStudentInfoNotDuplicates = new ArrayList<>(hashSet);

        ArrayList<StudentInfo> listStudentNotYes =
                (ArrayList<StudentInfo>) uniqueStudentInfo.stream()
                        .filter(p -> p.getHypothermia() > 0).collect(Collectors.toList());
        float studentNotYesPercentage =
                ((float) listStudentNotYes.size() / (float) uniqueStudentInfo.size()) * 100;
        float studentDonePercentage = 100 - studentNotYesPercentage;
        Log.d("TAG", "index=" + uniqueStudentInfo.size());
        Log.d("TAG", "index=" + listStudentNotYes.size());
        Log.d("TAG", "index=" + studentNotYesPercentage);
        ArrayList<PieEntry> yvalues = new ArrayList<>();
        yvalues.add(new PieEntry(studentNotYesPercentage,
                getString(R.string.temp_not_yes) + Constant.COMMA + listStudentNotYes.size() + Constant.COMMA_DIVISION + uniqueStudentInfo.size(), 0));
        yvalues.add(new PieEntry(studentDonePercentage,
                getString(R.string.temp_done) + Constant.COMMA + (uniqueStudentInfo.size() - listStudentNotYes.size()) + Constant.COMMA_DIVISION + uniqueStudentInfo.size(), 1));


        PieDataSet dataSet = new PieDataSet(yvalues, getString(R.string.hypothermia_results));
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

    }
}
