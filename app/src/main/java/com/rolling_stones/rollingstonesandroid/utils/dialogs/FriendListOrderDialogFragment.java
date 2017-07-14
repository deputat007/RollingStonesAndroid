package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.SettingsActivity;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;

import org.greenrobot.eventbus.EventBus;


public class FriendListOrderDialogFragment extends DialogFragment implements
        CompoundButton.OnCheckedChangeListener {

    public static DialogFragment newInstance() {
        return new FriendListOrderDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.content_radio_group, null);
        final AppCompatRadioButton radioButton1 = (AppCompatRadioButton)
                view.findViewById(R.id.item_first);
        final AppCompatRadioButton radioButton2 = (AppCompatRadioButton)
                view.findViewById(R.id.item_second);
        view.findViewById(R.id.item_third).setVisibility(View.GONE);

        final String[] array = getContext().getResources().getStringArray(R.array.list_order);
        radioButton1.setText(array[0]);
        radioButton2.setText(array[1]);

        switch (MyApplication.getApplication().getSharedPrefHelper().getListOrder()) {
            case 0:
                radioButton1.setChecked(true);
                break;

            case 1:
                radioButton2.setChecked(true);
                break;
        }

        radioButton1.setOnCheckedChangeListener(this);
        radioButton2.setOnCheckedChangeListener(this);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.text_friend_list_order)
                .setNegativeButton(R.string.text_button_cancel, null)
                .create();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_first:
                if (isChecked) {
                    MyApplication.getApplication().getSharedPrefHelper().saveListOrder(0);
                }
                break;

            case R.id.item_second:
                if (isChecked) {
                    MyApplication.getApplication().getSharedPrefHelper().saveListOrder(1);
                }
                break;
        }

        EventBus.getDefault().post(new SettingsActivity.FriendListOrderEvent());

        dismiss();
    }
}
