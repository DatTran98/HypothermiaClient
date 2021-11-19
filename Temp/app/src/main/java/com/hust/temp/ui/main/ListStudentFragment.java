package com.hust.temp.ui.main;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hust.temp.Common.Constant;
import com.hust.temp.R;
import com.hust.temp.entities.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListStudentFragment extends Fragment implements CustomDialogFilter.CustomDialogFilterListener {
    private ImageButton btnFilter;
    private ArrayList<Student> listStudentInfoSource = new ArrayList<>();
    private TableLayout tblStudents;
    private TextView txtFilter;
    private ProgressDialog loading;

    public static ListStudentFragment newInstance() {
        ListStudentFragment fragment = new ListStudentFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);
        setEvent();
        setData(listStudentInfoSource);
        GetListStudent getListStudent = new GetListStudent();
        getListStudent.execute();
    }

    private void setData(ArrayList<Student> listStudentInfo) {
        tblStudents.removeAllViews();
        if (listStudentInfo != null && !listStudentInfo.isEmpty()) {
            for (Student st : listStudentInfo) {
                TableRow tblRow = new TableRow(getContext());
                TextView t0v = new TextView(getContext());
                t0v.setText(st.getStudentID() + "");
                setTypeForView(t0v, true);
                tblRow.addView(t0v);
                TextView t1v = new TextView(getContext());
                t1v.setText(st.getStudentName());
                setTypeForView(t1v, false);
                tblRow.addView(t1v);
                TextView t2v = new TextView(getContext());
                t2v.setText(st.getStudentClass());
                setTypeForView(t2v, false);
                tblRow.addView(t2v);
                TextView t3v = new TextView(getContext());
                t3v.setText(st.getBirthday());
                setTypeForView(t3v, false);
                tblRow.addView(t3v);

                tblStudents.addView(tblRow);
            }
        } else {
            TableRow tblRow = new TableRow(getContext());
            TextView t0v = new TextView(getContext());
            t0v.setText(getResources().getString(R.string.no_data));
            tblRow.addView(t0v);
            setTypeForView(t0v, false);
            tblStudents.addView(tblRow);
        }
    }

    private void setTypeForView(TextView textView, boolean isIdView) {
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        if (isIdView) {
            layoutParams.weight = 2;
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
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = this.getActivity().getSupportFragmentManager();
        CustomDialogFilter editNameDialogFragment =
                CustomDialogFilter.newInstance(this.getResources().getString(R.string.filter_students), true);
        editNameDialogFragment.setTargetFragment(ListStudentFragment.this, 300);
        editNameDialogFragment.show(fm, "fragment_filter");
    }

    private void findViewById(View view) {
        btnFilter = view.findViewById(R.id.btn_filter);
        tblStudents = view.findViewById(R.id.tbl_student);
        txtFilter = view.findViewById(R.id.txt_filter);
    }

    @Override
    public void onFinishEditDialog(String inputTextName, String inputTextClass,
                                   String inputTextTempFrom, String inputTextTempTo,
                                   boolean sortDateIncrease, boolean sortDateDecrease,
                                   String textDate) {
        ArrayList<Student> filterSortedStudentInfo;
        String textFilter = "";
        if (inputTextName != null && !inputTextName.trim().isEmpty()) {
            filterSortedStudentInfo = (ArrayList<Student>) listStudentInfoSource.stream()
                    .filter(p -> p.getStudentName().contains(inputTextName)).collect(Collectors.toList());
            textFilter += getResources().getString(R.string.name) + inputTextName + getResources().getString(R.string.comma);
        } else {
            filterSortedStudentInfo = listStudentInfoSource;
        }
        if (inputTextClass != null && !inputTextClass.trim().isEmpty()) {
            filterSortedStudentInfo = (ArrayList<Student>) filterSortedStudentInfo.stream()
                    .filter(p -> p.getStudentClass().contains(inputTextClass)).collect(Collectors.toList());
            textFilter += getResources().getString(R.string.class_label) + inputTextClass + getResources().getString(R.string.comma_end_line);
        }
        tblStudents.removeAllViews();
        setData(filterSortedStudentInfo);
        txtFilter.setText(textFilter);
    }

    class GetListStudent extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    Constant.ROOT_URL_SUB2, response -> {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj != null) {
                        JSONArray jsonArrayRoom = jsonObj.getJSONArray(Constant.DATA_INFO);
                        for (int i = 0; i < jsonArrayRoom.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArrayRoom.get(i);
                            int id = Integer.parseInt(obj.getString(Constant.KEY_STUDENT_ID));

                            Student student = new Student(id,
                                    obj.getString(Constant.KEY_STUDENT_NAME),
                                    obj.getString(Constant.KEY_STUDENT_CLASS),
                                    obj.getString(Constant.KEY_STUDENT_BIRTHDAY));
                            listStudentInfoSource.add(student);
                        }
                        setData(listStudentInfoSource);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext().getApplicationContext(),
                            getResources().getString(R.string.can_trans_data), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }, error -> Toast.makeText(getContext().getApplicationContext(),
                    getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show()
            );
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(getActivity(),
                    getResources().getString(R.string.loading_data),
                    getResources().getString(R.string.waiting_minute), false, false);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            loading.dismiss();
        }
    }
}
