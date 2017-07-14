package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ApiHeaders;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.api.StatusCode;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.Token;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.WebApiDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout mTextInputLayoutLogin;
    private TextInputEditText mTextInputEditTextLogin;

    private TextInputLayout mTextInputLayoutPassword;
    private TextInputEditText mTextInputEditTextPassword;

    private Button mButtonSignIn;
    private Button mButtonSignUp;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, LoginActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initUI() {
        mTextInputLayoutLogin = findViewWithId(R.id.text_input_layout_login);
        mTextInputEditTextLogin = findViewWithId(R.id.edit_text_login);

        mTextInputLayoutPassword = findViewWithId(R.id.text_input_layout_password);
        mTextInputEditTextPassword = findViewWithId(R.id.edit_text_password);

        mButtonSignIn = findViewWithId(R.id.button_sign_in);
        mButtonSignUp = findViewWithId(R.id.button_sign_up);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {

        if (MyApplication.getApplication().isUserLogged()) {
            launchNextActivity();
            finish();
        }

        mButtonSignIn.setOnClickListener(this);
        mButtonSignUp.setOnClickListener(this);
        mTextInputEditTextLogin.addTextChangedListener(new TextValidator(mTextInputEditTextLogin));
        mTextInputEditTextPassword.addTextChangedListener(new TextValidator(mTextInputEditTextPassword));

        setTitle(R.string.title_activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_web_api_settings:
                final DialogFragment dialogFragment = WebApiDialogFragment.newInstance();
                dialogFragment.show(getSupportFragmentManager(),
                        Constants.TAG_DIALOG_WEB_API_SETTINGS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in:
                signIn();
                break;

            case R.id.button_sign_up:
                signUp();
                break;
        }
    }

    /**
     * The method calls {@link ApiHelper#getToken(String, String, Callback)} and
     * if response is successful {@link Response#isSuccessful()} then save this token {@link Token}
     * to shared preferences {@link android.content.SharedPreferences} and call {@link #launchNextActivity()}
     */
    private void signIn() {
        if (validateLogin() && validatePassword()) {
            final String login = mTextInputEditTextLogin.getText().toString();
            final String password = mTextInputEditTextPassword.getText().toString();

            showProgressDialog();
            ApiHelper.getToken(login, password, new MyCallback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MyApplication.getApplication()
                                .getSharedPrefHelper()
                                .saveLoggedUserId(Integer.parseInt(
                                        response.headers().get(ApiHeaders.USER_ID_HEADER)));

                        MyApplication.getApplication()
                                .getSharedPrefHelper()
                                .saveToken(response.body());

                        launchNextActivity();
                    }

                    super.onResponse(call, response);
                }

                @Override
                protected int handleError(int statusCode) {
                    switch (statusCode) {
                        case StatusCode.BAD_REQUEST:
                            return R.string.error_invalid_login_or_password;

                        default:
                            return super.handleError(statusCode);
                    }
                }
            });
        }
    }

    /**
     * The method launches {@link MainActivity}
     */
    private void launchNextActivity() {
        startActivity(MainActivity.getIntent(LoginActivity.this, null));
        finish();
    }

    /**
     * The method launches {@link RegistrationActivity}
     */
    private void signUp() {
        startActivity(RegistrationActivity.getIntent(this, null));
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

        mTextInputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private class TextValidator implements TextWatcher {

        private final View mView;

        private TextValidator(View view) {
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
            }
        }
    }
}
