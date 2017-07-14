package com.rolling_stones.rollingstonesandroid.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.activity.BaseActivity;
import com.rolling_stones.rollingstonesandroid.api.ErrorHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Fragment has basic methods, which help to find views{@link #findViewById(int)},
 * Retrofit callback @{@link MyCallback} and others.
 *
 * @author Deputat
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;

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
    protected abstract void setUI(@Nullable final Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;

        initUI();
        setUI(savedInstanceState);
    }

    @SuppressWarnings("unused")
    protected View getRootView() {
        return mRootView;
    }

    protected BaseActivity getParentActivity() {
        return ((BaseActivity) getActivity());
    }

    /**
     * This method can easy find your view @{@link View}, without casting;
     *
     * @param id  - view id;
     * @param <E> - View type, that will returned;
     * @return your view.
     */
    @SuppressWarnings("unchecked")
    public <E extends View> E findViewById(@IdRes final int id) {
        return (E) mRootView.findViewById(id);
    }

    /**
     * @param url image;
     * @return intent for action view
     */
    @Nullable
    protected Intent getIntentActionView(@NonNull String url) {
        final Intent intentActionView = new Intent(Intent.ACTION_VIEW);
        intentActionView.setData(Uri.parse(url));

        if (intentActionView.resolveActivity(getParentActivity().getPackageManager()) != null) {
            return intentActionView;
        }

        return null;
    }

    /**
     * Callback where we can handle errors and show Toast with this error @{@link Toast}.
     *
     * @param <T>
     */
    abstract class MyCallback<T> implements Callback<T> {

        public MyCallback() {
//            getParentActivity().showProgressDialog();
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (getParentActivity() != null) {
                getParentActivity().hideProgressDialog();
            }
            if (!response.isSuccessful() && response.message() != null) {
                Toast.makeText(getParentActivity(), handleError(response.code()),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (getParentActivity() != null) {
                getParentActivity().hideProgressDialog();
            }
            t.printStackTrace();
            Toast.makeText(getParentActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @StringRes
        protected int handleError(int statusCode) {
            return ErrorHandler.handleError(statusCode);
        }
    }
}
