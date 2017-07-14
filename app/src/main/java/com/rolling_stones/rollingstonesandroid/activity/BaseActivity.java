package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ErrorHandler;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.utils.SharedPreferencesHelper;
import com.rolling_stones.rollingstonesandroid.views.SpinnerDialog;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Activity has basic methods, which help to find views{@link #findViewWithId(int)},
 * show {@link #showProgressDialog()} or
 * hide progress bar dialog {@link #hideProgressDialog()}
 * and Retrofit callback @{@link MyCallback}.
 *
 * @author Deputat
 */
public abstract class BaseActivity extends AppCompatActivity {

    private SpinnerDialog mProgressDialog;

    /**
     * This method returns your layout;
     *
     * @return R.layout.your_layout.
     */
    @LayoutRes
    protected abstract int getContentView();

    /**
     * In this method you can find your views.
     */
    protected abstract void initUI();

    /**
     * In this method you can set some listeners or data.
     */
    protected abstract void setUI(final Bundle savedInstanceState);

    /**
     * This method can easy find your view @{@link View}, without casting;
     *
     * @param id  - view id;
     * @param <E> - View type, that will returned;
     * @return your view.
     */
    @SuppressWarnings("unchecked")
    public <E extends View> E findViewWithId(@IdRes final int id) {
        return (E) findViewById(id);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIsUserLogged();

        mProgressDialog = new SpinnerDialog(this);

        setContentView(getContentView());
        setToolbar();
        initUI();
        setUI(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onHomeAsUpClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method shows progress bar @{@link BaseActivity#mProgressDialog} if it is not shown.
     */
    public void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * This method hides progress bar @{@link BaseActivity#mProgressDialog} if it is shown.
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    /**
     * @return shared preferences helper @{@link SharedPreferencesHelper}.
     */
    public SharedPreferencesHelper getSharedPrefHelper() {
        return MyApplication.getApplication().getSharedPrefHelper();
    }

    /**
     * This method displays or hides HomeAsUp(<-);
     *
     * @param showHomeAsUp - show HomeAsUp(<-);
     */
    protected void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }

    /**
     * This method is called when HomeAsUp is clicked.
     */
    protected void onHomeAsUpClicked() {
        onBackPressed();
    }

    /**
     * The method requests focus to the @param view.
     *
     * @param view;
     */
    protected void requestFocus(@NonNull final View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * This method sets Toolbar @{@link Toolbar} if it exists in activity.
     */
    private void setToolbar() {
        final ActionBar actionBar = getSupportActionBar();
        final Toolbar toolbar = findViewWithId(R.id.toolbar);

        if (actionBar == null && toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    /**
     * This method checks is user logged, if not than call @{@link MyApplication#logout()}.
     */
    private void checkIsUserLogged() {
        if (!(this instanceof LoginActivity || this instanceof RegistrationActivity) &&
                getSharedPrefHelper().getLoggedUserId() == -1) {
            MyApplication.getApplication().logout();
        }
    }

    /**
     * @param url - image url;
     * @return intent for reviewing image
     */
    @Nullable
    protected Intent getIntentActionView(@NonNull String url) {
        final Intent intentActionView = new Intent(Intent.ACTION_VIEW);
        intentActionView.setData(Uri.parse(url));

        if (intentActionView.resolveActivity(getPackageManager()) != null) {
            return intentActionView;
        }

        return null;
    }

    /**
     * @return intent {@link RecognizerIntent#ACTION_RECOGNIZE_SPEECH}
     */
    protected Intent getRecognizeSpeechIntent() {
        return new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM).
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                .putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.text_speech_prompt));
    }

    /**
     * Hides the soft keyboard
     */
    protected void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    protected void showSoftKeyboard(@NonNull final View view) {
        final InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * Callback where we can handle errors and show Toast with this error @{@link Toast}.
     *
     * @param <T>
     */
    abstract class MyCallback<T> implements Callback<T> {

        public MyCallback() {
//            showProgressDialog();
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            hideProgressDialog();

            if (!response.isSuccessful() && response.message() != null) {
                Toast.makeText(BaseActivity.this, handleError(response.code()),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            hideProgressDialog();
            t.printStackTrace();
            Toast.makeText(BaseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @StringRes
        protected int handleError(int statusCode) {
            return ErrorHandler.handleError(statusCode);
        }
    }
}
