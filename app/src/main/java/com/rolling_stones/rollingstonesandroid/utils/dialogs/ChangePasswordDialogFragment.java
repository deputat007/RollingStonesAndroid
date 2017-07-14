package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordDialogFragment extends DialogFragment {

    public static DialogFragment newInstance() {
        return new ChangePasswordDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.content_change_password, null);

        final TextInputEditText textInputEditTextOldPassword = (TextInputEditText)
                view.findViewById(R.id.text_input_edit_text_old_password);

        final TextInputLayout textInputLayoutNewPassword =
                (TextInputLayout) view.findViewById(R.id.text_input_layout_new_password);

        final TextInputEditText textInputEditTextNewPassword = (TextInputEditText)
                view.findViewById(R.id.text_input_edit_text_new_password);

        final TextInputEditText textInputEditTextConfirmPassword = (TextInputEditText)
                view.findViewById(R.id.text_input_edit_text_confirm_password);

        final TextInputLayout textInputLayoutConfirmPassword =
                (TextInputLayout) view.findViewById(R.id.text_input_layout_confirm_password);

        textInputEditTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String newPassword =
                        textInputEditTextNewPassword.getText().toString().trim();

                validatePassword(newPassword, textInputLayoutNewPassword,
                        textInputEditTextNewPassword);
            }
        });
        textInputEditTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String confirmPassword =
                        textInputEditTextConfirmPassword.getText().toString().trim();
                final String newPassword =
                        textInputEditTextNewPassword.getText().toString().trim();

                validateConfirmPassword(newPassword, confirmPassword,
                        textInputLayoutConfirmPassword, textInputEditTextConfirmPassword);
            }
        });

        final Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.text_button_ok, null)
                .setNegativeButton(R.string.text_button_cancel, null)
                .setTitle(R.string.text_change_password)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String oldPassword =
                                textInputEditTextOldPassword.getText().toString().trim();
                        final String newPassword =
                                textInputEditTextNewPassword.getText().toString().trim();
                        final String confirmPassword =
                                textInputEditTextConfirmPassword.getText().toString().trim();
                        if (validatePassword(newPassword, textInputLayoutNewPassword,
                                textInputEditTextNewPassword) &&
                                validateConfirmPassword(
                                        newPassword, confirmPassword, textInputLayoutConfirmPassword,
                                        textInputEditTextConfirmPassword)) {
                            changePassword(oldPassword, newPassword);
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private void changePassword(String oldPassword, String newPassword) {
        ApiHelper.updateUserPassword(MyApplication.getApplication().getLoggedUserId(),
                oldPassword, newPassword, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), getString(R.string.text_password_changed),
                                    Toast.LENGTH_SHORT).show();
                            dismiss();
                            MyApplication.getApplication().logout();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.text_wrong_old_password),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), t.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validatePassword(final String password, final TextInputLayout inputLayout,
                                     final TextInputEditText editText) {
        if (TextUtils.isEmpty(password)) {
            inputLayout.setError(getString(R.string.error_password_is_empty));
            requestFocus(editText);
            return false;
        }

        if (password.length() < 3) {
            inputLayout.setError(getString(R.string.error_password_is_too_small));
            requestFocus(editText);
            return false;
        }

        inputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateConfirmPassword(final String password, final String confirmPassword,
                                            final TextInputLayout inputLayout,
                                            final TextInputEditText editText) {
        if (!password.equals(confirmPassword)) {
            inputLayout.setError(
                    getString(R.string.error_password_does_not_match_the_confirm_password));
            requestFocus(editText);
            return false;
        }

        inputLayout.setErrorEnabled(false);
        return true;
    }

    protected void requestFocus(@NonNull final View view) {
        if (view.requestFocus()) {
            getActivity().getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
