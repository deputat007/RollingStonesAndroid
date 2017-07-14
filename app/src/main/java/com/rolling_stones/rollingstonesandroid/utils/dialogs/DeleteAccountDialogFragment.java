package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.SettingsActivity;

import org.greenrobot.eventbus.EventBus;


public class DeleteAccountDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new DeleteAccountDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_delete_account)
                .setMessage(R.string.text_delete_account)
                .setNegativeButton(R.string.text_button_cancel, null)
                .setPositiveButton(R.string.text_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new SettingsActivity.DeleteAccountEvent());
                    }
                })
                .create();
    }
}
