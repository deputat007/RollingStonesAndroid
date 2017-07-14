package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;


public class WebApiDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new WebApiDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.content_web_api, null);

        final EditText editText = (EditText) view.findViewById(R.id.et_base_url);
        editText.setText(MyApplication.getApplication().getSharedPrefHelper().getUrl());

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(R.string.title_web_api)
                .setNegativeButton(R.string.text_button_cancel, null)
                .setPositiveButton(R.string.text_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication
                                .getApplication()
                                .getSharedPrefHelper()
                                .saveUrl(editText.getText().toString());
                    }
                })
                .create();
    }
}
