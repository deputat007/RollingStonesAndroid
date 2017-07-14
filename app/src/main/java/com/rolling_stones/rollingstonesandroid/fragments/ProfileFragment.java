package com.rolling_stones.rollingstonesandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.EditProfileActivity;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;


public class ProfileFragment extends NavigationTabFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private CircleImageView mUserIcon;
    private TextView mTextViewFirstAndLastName;
    private TextView mTextViewGender;
    private TextView mTextViewAge;
    private FloatingActionButton mFloatingActionButtonEdit;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mTextViewOnlineStatus;
    private ImageView mImageViewOnlineStatus;

    public static Fragment getInstance(@Nullable Bundle args) {
        final Fragment fragment = new ProfileFragment();

        if (args != null) {
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initUI() {
        mUserIcon = findViewById(R.id.user_icon);
        mTextViewFirstAndLastName = findViewById(R.id.tv_first_last_name);
        mTextViewGender = findViewById(R.id.tv_gender);
        mTextViewAge = findViewById(R.id.tv_age);
        mFloatingActionButtonEdit = findViewById(R.id.fab_edit);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mTextViewOnlineStatus = findViewById(R.id.tv_online_status);
        mImageViewOnlineStatus = findViewById(R.id.iv_online_status);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setUI(@Nullable Bundle savedInstanceState) {
        mFloatingActionButtonEdit.setOnClickListener(this);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);
        mUserIcon.setOnClickListener(this);
        setInformationAboutUser();
    }

    @Override
    public int getTitle() {
        return R.string.title_fragment_profile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_edit:
                startActivity(EditProfileActivity.getIntent(getParentActivity(), null));
                break;

            case R.id.user_icon:
                ApiHelper.getUserById(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                        new MyCallback<UserBase>() {
                            @Override
                            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    if (response.body().getPhotoUrl() != null) {
                                        startActivity(getIntentActionView(response.body().getPhotoUrl()));
                                    }
                                }

                                super.onResponse(call, response);
                            }
                        });
                break;
        }
    }

    @Override
    public void onRefresh() {
        setInformationAboutUser();
    }

    /**
     * The method is calling when user update his account {@link EditProfileActivity}
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(UserUpdateEvent event) {
        setInformationAboutUser();
    }

    /**
     * The method sets information about user
     */
    private void setInformationAboutUser() {
        mRefreshLayout.setRefreshing(true);
        ApiHelper.getUserById(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                new MyCallback<UserBase>() {
                    @Override
                    public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            IconUtils.setUserIcon(response.body(), mUserIcon);

                            mTextViewFirstAndLastName.setText(response.body().getFirstAndLastName());
                            mTextViewGender.setText(response.body().getGender().getStringRes());
                            mTextViewAge.setText(response.body().getAge(getContext()));

                            if (response.body().isOnline()) {
                                mImageViewOnlineStatus.setVisibility(View.VISIBLE);
                                mTextViewOnlineStatus.setText(R.string.text_online);
                            } else {
                                mImageViewOnlineStatus.setVisibility(View.GONE);
                                mTextViewOnlineStatus.setText(R.string.text_offline);
                            }
                        }

                        mRefreshLayout.setRefreshing(false);
                        super.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<UserBase> call, Throwable t) {
                        mRefreshLayout.setRefreshing(false);
                        super.onFailure(call, t);
                    }
                });

    }

    /**
     * Event for updating user information when user update his account {@link EditProfileActivity}
     */
    public static class UserUpdateEvent {
    }
}
