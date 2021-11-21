package com.hust.temp.ui.main;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hust.temp.R;
import com.hust.temp.entities.Student;

import java.util.ArrayList;

public class ListStudentAdapter extends BaseAdapter {
    private ArrayList<Student> listStudents;

    public ListStudentAdapter(ArrayList<Student> listStudentsAdapter) {
        this.listStudents = listStudentsAdapter;
    }

    @Override
    public int getCount() {
        return listStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return listStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listStudents.get(position).getStudentID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View accView;
        if (convertView == null) {
            accView = View.inflate(parent.getContext(), R.layout.item_user, null);
        } else accView = convertView;

        Student student = (Student) getItem(position);

        if (student == null) {
            TextView textView = accView.findViewById(R.id.studentName);
            textView.setText(parent.getContext().getResources().getString(R.string.no_data));
            textView.setGravity(Gravity.CENTER);
        } else {
            ((TextView) accView.findViewById(R.id.studentID)).setText(String.format("%s: %s",parent.getContext().getResources().getString(R.string.lbId),student.getStudentID()+""));
            ((TextView) accView.findViewById(R.id.studentName)).setText(student.getStudentName());
            ((TextView) accView.findViewById(R.id.studentClass)).setText(String.format("%s: %s",
                    parent.getContext().getResources().getString(R.string.lb_class),
                    student.getStudentClass()));
            ((TextView) accView.findViewById(R.id.birthday)).setText(String.format("%s: %s",
                    parent.getContext().getResources().getString(R.string.lb_birthday),
                    student.getBirthday()));
        }
        accView.findViewById(R.id.btnDelete).setVisibility(View.INVISIBLE);

        return accView;
    }
}
