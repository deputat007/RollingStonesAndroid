package com.rolling_stones.rollingstonesandroid.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.fragments.ProfileFragment;
import com.rolling_stones.rollingstonesandroid.models.Gender;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.DatePickerDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_DOB_DATE = "KEY_DOB_DATE";

    private CircleImageView mUserIcon;

    private TextInputLayout mTextInputLayoutFirstName;
    private TextInputEditText mTextInputEditTextFirstName;

    private TextInputLayout mTextInputLayoutLastName;
    private TextInputEditText mTextInputEditTextLastName;

    private AppCompatRadioButton mRadioButtonMale;
    private AppCompatRadioButton mRadioButtonFemale;

    private TextView mTextViewDOB;
    private ImageView mImageViewDOB;

    private Button mButtonSave;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, EditProfileActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void initUI() {
        mUserIcon = findViewWithId(R.id.user_icon);

        mTextInputLayoutFirstName = findViewWithId(R.id.text_input_layout_first_name);
        mTextInputEditTextFirstName = findViewWithId(R.id.edit_text_first_name);

        mTextInputLayoutLastName = findViewWithId(R.id.text_input_layout_last_name);
        mTextInputEditTextLastName = findViewWithId(R.id.edit_text_last_name);

        mRadioButtonMale = findViewWithId(R.id.radio_male);
        mRadioButtonFemale = findViewWithId(R.id.radio_female);

        mTextViewDOB = findViewWithId(R.id.tv_dob);
        mImageViewDOB = findViewWithId(R.id.iv_dob);

        mButtonSave = findViewWithId(R.id.button_save);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        setTitle(R.string.title_activity_edit_profile);
        setDisplayHomeAsUpEnabled(true);

        mUserIcon.setOnClickListener(this);
        mTextViewDOB.setOnClickListener(this);
        mImageViewDOB.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);

        mTextInputEditTextFirstName.addTextChangedListener(
                new TextValidator(mTextInputEditTextFirstName));
        mTextInputEditTextLastName.addTextChangedListener(
                new TextValidator(mTextInputEditTextLastName));

        setInformationAboutUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * TODO: add user icon
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_icon:
                break;

            case R.id.tv_dob:
                showDatePicker();
                break;

            case R.id.iv_dob:
                showDatePicker();
                break;

            case R.id.button_save:
                save();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!TextUtils.isEmpty(mTextViewDOB.getText().toString())) {
            outState.putString(KEY_DOB_DATE, mTextViewDOB.getText().toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(KEY_DOB_DATE)) {
            mTextViewDOB.setText(savedInstanceState.getString(KEY_DOB_DATE));
        }
    }

    /**
     * The method calls when user pick a dob date {@link DatePickerDialogFragment};
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onDatePickerEvent(RegistrationActivity.DatePickerEvent event) {
        mTextViewDOB.setText(DateFormatter.parseDate(event.getCalendar().getTime(), this));
    }

    /**
     * The method sets information about user
     */
    private void setInformationAboutUser() {
        ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(), new MyCallback<UserBase>() {
            @Override
            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final UserBase user = response.body();
                    final Gender gender = user.getGender();

                    IconUtils.setUserIcon(response.body(), mUserIcon);

                    mTextInputEditTextFirstName.setText(user.getFirstName());
                    mTextInputEditTextLastName.setText(user.getLastName());
                    mTextViewDOB.setText(DateFormatter.parseDate(user.getDateOfBirth(),
                            EditProfileActivity.this));

                    if (gender == Gender.FEMALE) {
                        mRadioButtonFemale.setChecked(true);
                    } else {
                        mRadioButtonMale.setChecked(true);
                    }
                }

                super.onResponse(call, response);
            }
        });

    }

    /**
     * The method shows date picker(dob) {@link DatePickerDialogFragment};
     */
    private void showDatePicker() {
        DialogFragment datePickerDialog;

        if (!TextUtils.isEmpty(mTextViewDOB.getText().toString())) {
            final Date date = DateFormatter.parseString(mTextViewDOB.getText().toString(), this);

            datePickerDialog = DatePickerDialogFragment.newInstance(
                    date != null ? date.getTime() : -1);
        } else {
            datePickerDialog = DatePickerDialogFragment.newInstance(-1);
        }

        datePickerDialog.show(getSupportFragmentManager(), Constants.TAG_DIALOG_DATE_PICKER);
    }

    /**
     * The method validates data({@link #validateFirstName()}, {@link #validateLastName()},
     * {@link #validateDateOfBirth()}) and calls {@link ApiHelper#updateUser(UserBase, Callback)};
     */
    private void save() {
        if (validateFirstName() && validateLastName() && validateDateOfBirth()) {

            final String firstName = mTextInputEditTextFirstName.getText().toString().trim();
            final String lastName = mTextInputEditTextLastName.getText().toString().trim();
            final Date dateOfBirth = DateFormatter.parseString(
                    mTextViewDOB.getText().toString().trim(), this);
            final Gender gender = (mRadioButtonMale.isChecked() && !mRadioButtonFemale.isChecked())
                    ? Gender.MALE : Gender.FEMALE;

            ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(),
                    new MyCallback<UserBase>() {
                        @Override
                        public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                final UserBase user = response.body();

                                user.setFirstName(firstName);
                                user.setLastName(lastName);
                                user.setGender(gender);
                                user.setDateOfBirth(dateOfBirth);

                                ApiHelper.updateUser(user, new MyCallback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            EventBus.getDefault().post(
                                                    new ProfileFragment.UserUpdateEvent());

                                            Toast.makeText(EditProfileActivity.this, R.string.text_account_updated,
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        super.onResponse(call, response);
                                    }
                                });
                            }

                            super.onResponse(call, response);
                        }
                    });
        }
    }

    /**
     * The method validates @{@link #mTextInputEditTextFirstName}
     *
     * @return true if data is valid
     */
    private boolean validateFirstName() {
        final String firstName = mTextInputEditTextFirstName.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            mTextInputLayoutFirstName.setError(getString(R.string.error_first_name_is_empty));
            requestFocus(mTextInputEditTextFirstName);
            return false;
        }

        mTextInputLayoutFirstName.setErrorEnabled(false);
        return true;
    }

    /**
     * The method validates @{@link #mTextInputEditTextLastName}
     *
     * @return true if data is valid
     */
    private boolean validateLastName() {
        final String lastName = mTextInputEditTextLastName.getText().toString().trim();

        if (TextUtils.isEmpty(lastName)) {
            mTextInputLayoutLastName.setError(getString(R.string.error_last_name_is_empty));
            requestFocus(mTextInputEditTextLastName);
            return false;
        }

        mTextInputLayoutLastName.setErrorEnabled(false);
        return true;
    }

    /**
     * The method validates @{@link #mTextViewDOB}
     *
     * @return true if data is valid
     */
    private boolean validateDateOfBirth() {
        final String dob = mTextViewDOB.getText().toString().trim();

        if (TextUtils.isEmpty(dob) || DateFormatter.parseString(dob, this) == null) {
            Toast.makeText(this, R.string.error_dob_is_not_selected, Toast.LENGTH_SHORT).show();
            requestFocus(mTextViewDOB);
            return false;
        }

        return true;
    }

    /**
     * Text watcher for data validation
     */
    private class TextValidator implements TextWatcher {

        private View mView;

        TextValidator(View view) {
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (mView.getId()) {
                case R.id.edit_text_first_name:
                    validateFirstName();
                    break;

                case R.id.edit_text_last_name:
                    validateLastName();
                    break;
            }
        }
    }
}
