package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.RegistrationActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DatePickerDialogFragment extends DialogFragment {

    private static final String KEY_CURRENT_DATE = "KEY_CURRENT_DATE";
    private GregorianCalendar mCalendar;

    public static DialogFragment newInstance(long currentDate) {
        final DialogFragment dialogFragment = new DatePickerDialogFragment();

        if (currentDate > 0) {
            final Bundle args = new Bundle();
            args.putLong(KEY_CURRENT_DATE, currentDate);
            dialogFragment.setArguments(args);
        }

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = new GregorianCalendar();

        if (getArguments() != null && getArguments().containsKey(KEY_CURRENT_DATE)) {
            mCalendar.setTimeInMillis(getArguments().getLong(KEY_CURRENT_DATE));
        } else {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);

        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);

        datePicker.init(year, month, day, null);
        datePicker.setMaxDate(new Date().getTime());

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.text_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCalendar.set(datePicker.getYear(), datePicker.getMonth(),
                                datePicker.getDayOfMonth());

                        EventBus.getDefault()
                                .post(new RegistrationActivity.DatePickerEvent(mCalendar));

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.text_button_cancel, null)
                .create();
    }
}
