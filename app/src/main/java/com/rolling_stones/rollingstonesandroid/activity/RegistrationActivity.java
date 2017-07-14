package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
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
import com.rolling_stones.rollingstonesandroid.api.StatusCode;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.models.Gender;
import com.rolling_stones.rollingstonesandroid.models.User;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;
import com.rolling_stones.rollingstonesandroid.utils.GlideLoader;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.DatePickerDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rolling_stones.rollingstonesandroid.application.Constants.REQUEST_IMAGE_CAPTURE;


public class RegistrationActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_DOB_DATE = "KEY_DOB_DATE";

    private CircleImageView mUserIcon;

    private TextInputLayout mTextInputLayoutLogin;
    private TextInputEditText mTextInputEditTextLogin;

    private TextInputLayout mTextInputLayoutPassword;
    private TextInputEditText mTextInputEditTextPassword;

    private TextInputLayout mTextInputLayoutConfirmPassword;
    private TextInputEditText mTextInputEditTextConfirmPassword;

    private TextInputLayout mTextInputLayoutFirstName;
    private TextInputEditText mTextInputEditTextFirstName;

    private TextInputLayout mTextInputLayoutLastName;
    private TextInputEditText mTextInputEditTextLastName;

    private AppCompatRadioButton mRadioButtonMale;
    private AppCompatRadioButton mRadioButtonFemale;

    private TextView mTextViewDOB;
    private ImageView mImageViewDOB;

    private AppCompatCheckBox mCheckBoxReceiveMessages;

    private Button mButtonCreateAccount;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, RegistrationActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_registration;
    }

    @Override
    protected void initUI() {
        mUserIcon = findViewWithId(R.id.user_icon);

        mTextInputLayoutLogin = findViewWithId(R.id.text_input_layout_login);
        mTextInputEditTextLogin = findViewWithId(R.id.edit_text_login);

        mTextInputLayoutPassword = findViewWithId(R.id.text_input_layout_password);
        mTextInputEditTextPassword = findViewWithId(R.id.edit_text_password);

        mTextInputLayoutConfirmPassword = findViewWithId(R.id.text_input_layout_confirm_password);
        mTextInputEditTextConfirmPassword = findViewWithId(R.id.edit_text_confirm_password);

        mTextInputLayoutFirstName = findViewWithId(R.id.text_input_layout_first_name);
        mTextInputEditTextFirstName = findViewWithId(R.id.edit_text_first_name);

        mTextInputLayoutLastName = findViewWithId(R.id.text_input_layout_last_name);
        mTextInputEditTextLastName = findViewWithId(R.id.edit_text_last_name);

        mRadioButtonMale = findViewWithId(R.id.radio_male);
        mRadioButtonFemale = findViewWithId(R.id.radio_female);

        mTextViewDOB = findViewWithId(R.id.tv_dob);
        mImageViewDOB = findViewWithId(R.id.iv_dob);

        mCheckBoxReceiveMessages = findViewWithId(R.id.checkbox_receive_messages);

        mButtonCreateAccount = findViewWithId(R.id.button_create_account);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        setTitle(R.string.title_activity_registration);
        setDisplayHomeAsUpEnabled(true);

        mTextViewDOB.setText(DateFormatter.DATE_FORMAT_PATTERN);

        mUserIcon.setOnClickListener(this);
        mTextViewDOB.setOnClickListener(this);
        mImageViewDOB.setOnClickListener(this);
        mButtonCreateAccount.setOnClickListener(this);

        mTextInputEditTextLogin.addTextChangedListener(
                new TextValidator(mTextInputEditTextLogin));
        mTextInputEditTextFirstName.addTextChangedListener(
                new TextValidator(mTextInputEditTextFirstName));
        mTextInputEditTextLastName.addTextChangedListener(
                new TextValidator(mTextInputEditTextLastName));
        mTextInputEditTextPassword.addTextChangedListener(
                new TextValidator(mTextInputEditTextPassword));
        mTextInputEditTextConfirmPassword.addTextChangedListener(
                new TextValidator(mTextInputEditTextConfirmPassword));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_icon:
                dispatchTakePictureIntent();
                break;

            case R.id.tv_dob:
                showDatePicker();
                break;

            case R.id.iv_dob:
                showDatePicker();
                break;

            case R.id.button_create_account:
                addNewUser();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            GlideLoader.loadImage(mUserIcon.getContext(), mUserIcon, uri);
        }
    }

    /**
     * The method calls when user pick a dob date {@link DatePickerDialogFragment};
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onDatePickerEvent(DatePickerEvent event) {
        mTextViewDOB.setText(DateFormatter.parseDate(event.getCalendar().getTime(), this));
    }

    /**
     * The method launches intent for taking picture;
     */
    private void dispatchTakePictureIntent() {
        final Intent takePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        takePictureIntent.setType("image/*");

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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
     * The method validates data({@link #validateLogin()}, {@link #validatePassword()},
     * {@link #validateConfirmPassword()}{@link #validateFirstName()}, {@link #validateLastName()},
     * {@link #validateDateOfBirth()}) and calls {@link ApiHelper#createUser(User, Callback)};
     */
    private void addNewUser() {
        if (validateLogin() && validatePassword() && validateConfirmPassword() &&
                validateFirstName() && validateLastName() && validateDateOfBirth()) {

            final String login = mTextInputEditTextLogin.getText().toString().trim();
            final String password = mTextInputEditTextPassword.getText().toString().trim();
            final String firstName = mTextInputEditTextFirstName.getText().toString().trim();
            final String lastName = mTextInputEditTextLastName.getText().toString().trim();
            final Date dateOfBirth = DateFormatter.parseString(
                    mTextViewDOB.getText().toString().trim(), this);
            final Gender gender = (mRadioButtonMale.isChecked() && !mRadioButtonFemale.isChecked())
                    ? Gender.MALE : Gender.FEMALE;
            final boolean receiveOnlyFriendsMessages = mCheckBoxReceiveMessages.isChecked();

            final User user = new User(2, firstName, lastName, login, gender, dateOfBirth,
                    receiveOnlyFriendsMessages, password);

            ApiHelper.createUser(user, new MyCallback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, R.string.text_account_created,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    super.onResponse(call, response);
                }

                @Override
                protected int handleError(int statusCode) {
                    switch (statusCode) {
                        case StatusCode.CONFLICT:
                            return R.string.error_login_already_exists;

                        default:
                            return super.handleError(statusCode);
                    }
                }
            });
        }
    }

    /**
     * The method validates @{@link #mTextInputEditTextLogin}
     *
     * @return true if data is valid
     */
    private boolean validateLogin() {
        final String login = mTextInputEditTextLogin.getText().toString().trim();

        if (TextUtils.isEmpty(login)) {
            mTextInputLayoutLogin.setError(getString(R.string.error_login_is_empty));
            requestFocus(mTextInputEditTextLogin);
            return false;
        }

        if (login.length() < 3) {
            mTextInputLayoutLogin.setError(getString(R.string.error_login_is_too_small));
            requestFocus(mTextInputEditTextLogin);
            return false;
        }

        mTextInputLayoutLogin.setErrorEnabled(false);
        return true;
    }

    /**
     * The method validates @{@link #mTextInputEditTextPassword}
     *
     * @return true if data is valid
     */
    private boolean validatePassword() {
        final String password = mTextInputEditTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            mTextInputLayoutPassword.setError(getString(R.string.error_password_is_empty));
            requestFocus(mTextInputEditTextPassword);
            return false;
        }

        if (password.length() < 3) {
            mTextInputLayoutPassword.setError(getString(R.string.error_password_is_too_small));
            requestFocus(mTextInputEditTextPassword);
            return false;
        }

        mTextInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    /**
     * The method validates @{@link #mTextInputEditTextConfirmPassword}
     *
     * @return true if data is valid
     */
    private boolean validateConfirmPassword() {
        final String password = mTextInputEditTextPassword.getText().toString().trim();
        final String confirmPassword = mTextInputEditTextConfirmPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            mTextInputLayoutConfirmPassword.setError(
                    getString(R.string.error_password_does_not_match_the_confirm_password));
            requestFocus(mTextInputEditTextConfirmPassword);
            return false;
        }

        mTextInputLayoutConfirmPassword.setErrorEnabled(false);
        return true;
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
                case R.id.edit_text_login:
                    validateLogin();
                    break;

                case R.id.edit_text_password:
                    validatePassword();
                    break;

                case R.id.edit_text_confirm_password:
                    validateConfirmPassword();
                    break;

                case R.id.edit_text_first_name:
                    validateFirstName();
                    break;

                case R.id.edit_text_last_name:
                    validateLastName();
                    break;
            }
        }
    }

    /**
     * Event for {@link DatePickerDialogFragment}
     */
    public static class DatePickerEvent {
        private final GregorianCalendar mCalendar;

        public DatePickerEvent(GregorianCalendar calendar) {
            mCalendar = calendar;
        }

        GregorianCalendar getCalendar() {
            return mCalendar;
        }
    }
}
