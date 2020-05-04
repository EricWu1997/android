package com.example.photogalleryapp.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.photogalleryapp.R;
import com.example.photogalleryapp.util.DateParser;

import java.util.Calendar;
import java.util.Date;

public class SearchFragment extends DialogFragment {

    private Calendar calender = Calendar.getInstance();

    private EditText text_keyword;
    private EditText text_dateStart;
    private EditText text_dateEnd;

    private Date start = null;
    private Date end = null;

    private OnInputListener onInputListener;

    public interface OnInputListener {
        void onFilterSet(String keyword, Date start, Date end);
    }

    private DatePickerDialog.OnDateSetListener startDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            calender.set(Calendar.YEAR, year);
            calender.set(Calendar.MONTH, monthOfYear);
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateStartField(calender.getTime());
        }

    };

    private DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calender.set(Calendar.YEAR, year);
            calender.set(Calendar.MONTH, monthOfYear);
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateEndField(calender.getTime());
        }

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_popup, container, false);

        text_keyword = view.findViewById(R.id.text_keyword);
        text_dateStart = view.findViewById(R.id.text_dateStart);
        text_dateEnd = view.findViewById(R.id.text_dateEnd);

        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);

        Button button_confirm = view.findViewById(R.id.button_confirm);
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputListener.onFilterSet(
                        text_keyword.getText().toString(), start, end);
                getDialog().dismiss();
            }
        });

        text_dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateStartField(null);
                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), startDatePicker, calender
                        .get(Calendar.YEAR), calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH));
                if (end != null)
                    dialog.getDatePicker().setMaxDate(end.getTime());
                dialog.show();
            }
        });

        text_dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateEndField(null);
                setCalenderToCurrentDate();
                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), endDatePicker, calender
                        .get(Calendar.YEAR), calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH));
                if (start != null)
                    dialog.getDatePicker().setMinDate(start.getTime());
                dialog.show();
            }
        });

        return view;
    }

    private void setCalenderToCurrentDate() {
        calender = Calendar.getInstance();
    }

    private void updateEndField(Date date) {
        end = date;
        String date_text;
        if (date == null) {
            date_text = "";
        } else {
            date_text = DateParser.parseDate(date);
        }
        text_dateEnd.setText(date_text);
    }

    private void updateStartField(Date date) {
        start = date;
        String date_text;
        if (date == null) {
            date_text = "";
        } else {
            date_text = DateParser.parseDate(date);
        }
        text_dateStart.setText(date_text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("onAttach", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
