package com.hust.temp.ui.main;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hust.temp.Common.Constant;
import com.hust.temp.R;
import com.hust.temp.entities.StudentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hust.temp.R.id;
import static com.hust.temp.R.layout;
import static com.hust.temp.R.string;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ListHypothermiaFragment extends Fragment implements CustomDialogFilter.CustomDialogFilterListener {
    private ImageButton btnFilter;
    private TableLayout tblHypothermia;
    private ArrayList<StudentInfo> listStudentInfoSource = new ArrayList<>();
    private TextView txtFilter;
    private ProgressDialog loading;
    private Boolean isWarning;

    public ListHypothermiaFragment(boolean isWarning) {
        this.isWarning =isWarning;
    }

    public static ListHypothermiaFragment newInstance(boolean isWarning) {
        return new ListHypothermiaFragment(isWarning);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);
        setEvent();

        listStudentInfoSource = new ArrayList<>();
        setData(listStudentInfoSource);
        getListStudentInfo();
    }

    private void setData(ArrayList<StudentInfo> listStudentInfo) {
        tblHypothermia.removeAllViews();
        if (listStudentInfo != null && !listStudentInfo.isEmpty()) {

            for (StudentInfo st : listStudentInfo) {
                TableRow tblRow = new TableRow(getContext());
                TextView t1v = new TextView(getContext());
                t1v.setText(st.getStudentName());
                setTypeForView(t1v, true);
                tblRow.addView(t1v);
                TextView t2v = new TextView(getContext());
                t2v.setText(st.getStudentClass());
                setTypeForView(t2v, false);
                tblRow.addView(t2v);
                TextView t3v = new TextView(getContext());
                t3v.setText(st.getBirthday());
                setTypeForView(t3v, false);
                tblRow.addView(t3v);
                TextView t4v = new TextView(getContext());
                t4v.setText(st.getHypothermia() + "");
                setTypeForView(t4v, false);
                if (isWarning){
                    t4v.setTextColor(Color.RED);
                }
                tblRow.addView(t4v);
                TextView t5v = new TextView(getContext());
                t5v.setText(new SimpleDateFormat(Constant.FORMAT_PARTEN, Locale.ROOT).format(st.getLastUpdatedDate()));
                setTypeForView(t5v, true);
                tblRow.addView(t5v);
                tblHypothermia.addView(tblRow);
            }
        } else {
            TableRow tblRow = new TableRow(getContext());
            TextView t0v = new TextView(getContext());
            t0v.setText(getResources().getString(R.string.no_data));
            tblRow.addView(t0v);
            setTypeForView(t0v, false);
            tblHypothermia.addView(tblRow);
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

    private void setEvent() {
        btnFilter.setOnClickListener(v -> showEditDialog());
    }

    private void findViewById(View view) {
        btnFilter = view.findViewById(id.btn_filter);
        tblHypothermia = view.findViewById(id.tbl_hypothermia);
        txtFilter = view.findViewById(id.txt_filter);
    }

    private void showEditDialog() {
        FragmentManager fm = this.getActivity().getSupportFragmentManager();
        CustomDialogFilter editNameDialogFragment =
                CustomDialogFilter.newInstance(this.getResources().getString(string.filter_huypo)
                        , false);
        editNameDialogFragment.setTargetFragment(ListHypothermiaFragment.this, 500);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditDialog(String inputTextName, String inputTextClass,
                                   String inputTextTempFrom, String inputTextTempTo,

                                   boolean sortDateIncrease, boolean sortDateDecrease,
                                   String textDate) {
        String textFilter = "";
        ArrayList<StudentInfo> filterSortedStudentInfo = listStudentInfoSource;
        if (inputTextName != null && !inputTextName.trim().isEmpty()) {
            filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                    .filter(p -> p.getStudentName().contains(inputTextName)).collect(Collectors.toList());
            textFilter += getResources().getString(R.string.name) + inputTextName + getResources().getString(R.string.comma);
        }

        if (inputTextClass != null && !inputTextClass.trim().isEmpty()) {
            filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                    .filter(p -> p.getStudentClass().contains(inputTextClass)).collect(Collectors.toList());
            textFilter += getResources().getString(R.string.class_label) + inputTextClass + getResources().getString(R.string.comma_end_line);
        }
        if (inputTextTempFrom != null && !inputTextTempFrom.trim().isEmpty()) {
            double parseTempFrom = Double.parseDouble(inputTextTempFrom);
            filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                    .filter(p -> p.getHypothermia() >= parseTempFrom).collect(Collectors.toList());
            textFilter += getResources().getString(string.temp_from) + parseTempFrom + getResources().getString(R.string.comma_end_line);
        }
        if (inputTextTempTo != null && !inputTextTempTo.trim().isEmpty()) {
            double parseTempTo = Double.parseDouble(inputTextTempTo);
            filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                    .filter(p -> p.getHypothermia() <= parseTempTo).collect(Collectors.toList());
            textFilter += getResources().getString(string.temp_to) + parseTempTo + getResources().getString(R.string.comma_end_line);
        }
        if (textDate != null && !textDate.trim().isEmpty()) {
            String startTimeString = textDate + " 00:00:00";
            String endTimeString = textDate + " 23:59:59";
            SimpleDateFormat format = new SimpleDateFormat(Constant.FORMAT_PARTEN, Locale.ROOT);
            try {
                Date startTime = format.parse(startTimeString);
                Date endTime = format.parse(endTimeString);
                filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                        .filter(p -> p.getLastUpdatedDate().getTime() <= endTime.getTime())
                        .filter(p -> p.getLastUpdatedDate().getTime() >= startTime.getTime())
                        .collect(Collectors.toList());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (sortDateIncrease) {
            filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                    .sorted(Comparator.comparing(StudentInfo::getLastUpdatedDate))
                    .collect(Collectors.toList());
            textFilter += getResources().getString(R.string.sort_date_increase);

        }
        if (sortDateDecrease) {
            filterSortedStudentInfo = (ArrayList<StudentInfo>) filterSortedStudentInfo.stream()
                    .sorted(Comparator.comparing(StudentInfo::getLastUpdatedDate).reversed())
                    .collect(Collectors.toList());
            Collections.reverse(filterSortedStudentInfo);
            textFilter += getResources().getString(R.string.sort_date_decrease);
        }
        tblHypothermia.removeAllViews();
        setData(filterSortedStudentInfo);
        txtFilter.setText(textFilter);
    }


    private void getListStudentInfo() {
        SharedPreferences mPreferences = getActivity().getSharedPreferences(Constant.SHARED_PROFILE, MODE_PRIVATE);
        String role = mPreferences.getString(Constant.KEY_ROLE_SIGNED, Constant.FALSE);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constant.ROOT_URL_SUB1, response -> {
            loading.dismiss();
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONArray jsonArrayRoom = jsonObj.getJSONArray(Constant.DATA_INFO);
                for (int i = 0; i < jsonArrayRoom.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArrayRoom.get(i);
                    Date date;
                    double tempValue;
                    long id;
                    if (!obj.getString(Constant.KEY_HYPOTHERMIA_LAST_UPDATE).equals(
                            "null")) {
                        date = new java.text.SimpleDateFormat(Constant.FORMAT_PARTEN, Locale.ROOT).parse(obj.getString(Constant.KEY_HYPOTHERMIA_LAST_UPDATE));
                    } else {
                        date = new java.text.SimpleDateFormat(Constant.FORMAT_PARTEN, Locale.ROOT).parse(Constant.DATE_DEFAULT);
                    }
                    if (!(obj.getString(Constant.KEY_HYPOTHERMIA_VALUE).equals(Constant.NULL_VALUE))) {
                        tempValue =
                                Double.parseDouble(obj.getString(Constant.KEY_HYPOTHERMIA_VALUE));
                    } else {
                        tempValue = 0;
                    }
                    if (!(obj.getString(Constant.KEY_STUDENT_ID).equals(Constant.NULL_VALUE))) {
                        id = Long.parseLong(obj.getString(Constant.KEY_STUDENT_ID));
                    } else {
                        id = 0;
                    }

                    StudentInfo studentInfo = new StudentInfo(id,
                            obj.getString(Constant.KEY_STUDENT_NAME),
                            obj.getString(Constant.KEY_STUDENT_CLASS),
                            obj.getString(Constant.KEY_STUDENT_BIRTHDAY), tempValue,
                            date);
                    listStudentInfoSource.add(studentInfo);
                }
                if(isWarning){
                    listStudentInfoSource = (ArrayList<StudentInfo>) listStudentInfoSource.stream()
                            .filter(p -> p.getHypothermia() >= 37).collect(Collectors.toList());
                }
                setData(listStudentInfoSource);
                getActivity().getIntent().putExtra(Constant.KEY_LIST_HYPOTHERMIA, listStudentInfoSource);
            } catch (JSONException | ParseException e) {
                loading.dismiss();
                Toast.makeText(getContext().getApplicationContext(),
                        getResources().getString(string.can_trans_data),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }, error ->
                Toast.makeText(getContext().getApplicationContext(),
                        getResources().getString(string.server_error), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.KEY_ROLE, role);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
        loading = ProgressDialog.show(getActivity(),
                getResources().getString(R.string.loading_data),
                getResources().getString(R.string.waiting_minute), false, false);
    }
}