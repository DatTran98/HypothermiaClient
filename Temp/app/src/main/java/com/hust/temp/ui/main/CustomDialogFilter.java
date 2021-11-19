package com.hust.temp.ui.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.hust.temp.AddStudentActivity;
import com.hust.temp.Common.Constant;
import com.hust.temp.R;

import java.util.Calendar;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class CustomDialogFilter extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText filterStudentName, filterClass, filterTempFrom, filterTemTo, editTextDate;
    private TextInputLayout textInputTempFrom, textInputTempTo;
    private MaterialTextView textInputSortByTemp, textInputSortByDate, titleDialog;
    private RadioGroup radioGroupDate, radioGroupTemp;
    private RadioButton sortByTempIncrease, sortByTempDecrease, sortByDateIncrease,
            sortByDateDecrease;
    private CircularProgressButton btnSearch;
    private ImageButton btnPickDate;
    private View searchTextDate;

    public interface CustomDialogFilterListener {
        void onFinishEditDialog(String inputTextName, String inputTextClass,
                                String inputTextTempFrom, String inputTextTempTo,
                                boolean sortDateIncrease, boolean sortDateDecrease,
                                String textDate);
    }

    public static CustomDialogFilter newInstance(String title, boolean studentDialogFilter) {

        CustomDialogFilter frag = new CustomDialogFilter();
        Bundle args = new Bundle();
        args.putString(Constant.DIALOG_TITLE, title);
        args.putBoolean(Constant.STUDENT_DIALOG_TYPE, studentDialogFilter);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_filter_dialog,
                container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        findViewById(view);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString(Constant.DIALOG_TITLE, Constant.FILTER_TITLE);
        boolean isStudentDialog = getArguments().getBoolean(Constant.STUDENT_DIALOG_TYPE, false);
        getDialog().setTitle(title);
        titleDialog.setText(title);
        if (isStudentDialog) {
            textInputTempFrom.setVisibility(View.GONE);
            textInputTempTo.setVisibility(View.GONE);
//            textInputSortByTemp.setVisibility(View.GONE);
            textInputSortByDate.setVisibility(View.GONE);
            radioGroupDate.setVisibility(View.GONE);
//            radioGroupTemp.setVisibility(View.GONE);
            searchTextDate.setVisibility(View.GONE);
        }
        filterStudentName.setOnEditorActionListener(this);
        filterTempFrom.setOnEditorActionListener(this);
        filterTemTo.setOnEditorActionListener(this);
        filterClass.setOnEditorActionListener(this);
        // Show soft keyboard automatically and request focus to field
        requestFocus(filterStudentName, filterTempFrom, filterTemTo, filterClass);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBackResult();
            }
        });
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSelectDate();
            }
        });
    }

    private void requestFocus(EditText filterStudentName, EditText filterTempFrom,
                              EditText filterTemTo, EditText filterClass) {
        filterStudentName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        filterTempFrom.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        filterTemTo.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        filterClass.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void findViewById(View view) {
        filterStudentName = (EditText) view.findViewById(R.id.txt_name);
        filterTempFrom = (EditText) view.findViewById(R.id.txt_temp_from);
        filterTemTo = (EditText) view.findViewById(R.id.txt_temp_to);
        filterClass = (EditText) view.findViewById(R.id.txt_class);
        textInputTempFrom = (TextInputLayout) view.findViewById(R.id.textInputTempFrom);
        textInputTempTo = (TextInputLayout) view.findViewById(R.id.textInputTempTo);
//        textInputSortByTemp = (MaterialTextView) view.findViewById(R.id.textInputSortByTemp);
        textInputSortByDate = (MaterialTextView) view.findViewById(R.id.textInputSortByDate);
        titleDialog = (MaterialTextView) view.findViewById(R.id.titleDialog);
//        radioGroupTemp = (RadioGroup) view.findViewById(R.id.radio_group_temp);
        radioGroupDate = (RadioGroup) view.findViewById(R.id.radio_group_date);
        btnSearch = (CircularProgressButton) view.findViewById(R.id.btnSearch);
//        sortByTempIncrease = (RadioButton) view.findViewById(R.id.sort_by_temp_increase);
//        sortByTempDecrease = (RadioButton) view.findViewById(R.id.sort_by_temp_decrease);
        sortByDateIncrease = (RadioButton) view.findViewById(R.id.sort_by_date_increase);
        sortByDateDecrease = (RadioButton) view.findViewById(R.id.sort_by_date_decrease);
        editTextDate = (EditText) view.findViewById(R.id.editTextDate);
        btnPickDate = (ImageButton) view.findViewById(R.id.btn_pick_date_dialog);
        searchTextDate = view.findViewById(R.id.textSearchDate);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            sendBackResult();
            // Close the dialog and return back to the parent activity
            this.dismiss();
            return true;
        }
        return false;
    }

    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        CustomDialogFilterListener listener = (CustomDialogFilterListener) getTargetFragment();
        listener.onFinishEditDialog(filterStudentName.getText().toString(),
                filterClass.getText().toString(), filterTempFrom.getText().toString(),
                filterTemTo.getText().toString(), sortByDateIncrease.isChecked(),
                sortByDateDecrease.isChecked(), editTextDate.getText().toString());
        dismiss();
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    private void buttonSelectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(CustomDialogFilter.this.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editTextDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
}
