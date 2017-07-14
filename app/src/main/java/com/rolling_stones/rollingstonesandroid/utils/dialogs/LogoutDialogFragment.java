package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;


public class LogoutDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new LogoutDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_logout)
                .setMessage(R.string.text_logout)
                .setNegativeButton(R.string.text_button_cancel, null)
                .setPositiveButton(R.string.text_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new MainActivity.LogoutEvent());
                    }
                })
                .create();
    }
}
