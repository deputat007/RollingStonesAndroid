package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.fragments.FriendsFragment;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.models.UserBaseWithState;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

    private CircleImageView mUserIcon;
    private TextView mTextViewFirstAndLastName;
    private TextView mTextViewGender;
    private TextView mTextViewAge;
    private Button mButtonMessage;
    private Button mButtonFriend;
    private Button mButtonRemoveRequest;
    private TextView mTextViewDistance;
    private ImageView mImageViewOnlineStatus;
    private TextView mTextViewOnlineStatus;

    private int mSelectedUserId;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, UserProfileActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected void initUI() {
        mUserIcon = findViewWithId(R.id.user_icon);
        mTextViewFirstAndLastName = findViewWithId(R.id.tv_first_last_name);
        mTextViewGender = findViewWithId(R.id.tv_gender);
        mTextViewAge = findViewWithId(R.id.tv_age);
        mButtonFriend = findViewWithId(R.id.button_friend);
        mButtonRemoveRequest = findViewWithId(R.id.button_remove_request);
        mButtonMessage = findViewWithId(R.id.button_message);
        mTextViewDistance = findViewWithId(R.id.tv_distance);
        mTextViewOnlineStatus = findViewWithId(R.id.tv_online_status);
        mImageViewOnlineStatus = findViewWithId(R.id.iv_online_status);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_activity_user_profile);
        mSelectedUserId = getIntent().getIntExtra(Constants.KEY_SELECTED_USER_ID, -1);

        setInformationAboutUser();
        mButtonFriend.setOnClickListener(this);
        mButtonMessage.setOnClickListener(this);
        mButtonFriend.setEnabled(false);
        mButtonMessage.setEnabled(false);
        mButtonRemoveRequest.setVisibility(View.GONE);
        mUserIcon.setOnClickListener(this);
        mButtonRemoveRequest.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_message:
                Bundle args = new Bundle();
                args.putInt(Constants.KEY_SELECTED_USER_ID, mSelectedUserId);

                startActivity(ChatActivity.getIntent(UserProfileActivity.this, args));
                break;

            case R.id.button_remove_request:
                ApiHelper.removeRequest(getSharedPrefHelper().getLoggedUserId(), mSelectedUserId,
                        new MyCallback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    setInformationAboutUser();
                                    Toast.makeText(UserProfileActivity.this,
                                            R.string.text_request_removed, Toast.LENGTH_SHORT).show();
                                }

                                super.onResponse(call, response);
                            }
                        });
                break;

            case R.id.button_friend:
                if (mButtonFriend.getText().toString().equals(
                        getString(R.string.text_add_as_a_friend))) {
                    ApiHelper.addRequest(getSharedPrefHelper().getLoggedUserId(), mSelectedUserId,
                            new MyCallback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        setInformationAboutUser();
                                        Toast.makeText(UserProfileActivity.this,
                                                R.string.text_request_added, Toast.LENGTH_SHORT).show();
                                    }
                                    super.onResponse(call, response);
                                }
                            });
                } else if (mButtonFriend.getText().toString().equals(
                        getString(R.string.text_remove_from_friends))) {
                    ApiHelper.removeFriends(getSharedPrefHelper().getLoggedUserId(),
                            mSelectedUserId, new MyCallback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        setInformationAboutUser();
                                        Toast.makeText(UserProfileActivity.this,
                                                R.string.text_friend_removed, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(
                                                new FriendsFragment.FriendsUpdateEvent());
                                    }
                                    super.onResponse(call, response);
                                }
                            });
                } else if (mButtonFriend.getText().toString().equals(
                        getString(R.string.text_remove_request))) {
                    ApiHelper.removeRequest(mSelectedUserId, getSharedPrefHelper().getLoggedUserId(),
                            new MyCallback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        setInformationAboutUser();
                                        Toast.makeText(UserProfileActivity.this,
                                                R.string.text_request_removed, Toast.LENGTH_SHORT).show();
                                    }

                                    super.onResponse(call, response);
                                }
                            });
                } else if (mButtonFriend.getText().toString().equals(
                        getString(R.string.text_accept_request))) {
                    ApiHelper.addFriends(getSharedPrefHelper().getLoggedUserId(), mSelectedUserId,
                            new MyCallback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        setInformationAboutUser();
                                        EventBus.getDefault().post(
                                                new FriendsFragment.FriendsUpdateEvent());
                                        Toast.makeText(UserProfileActivity.this,
                                                R.string.text_request_accepted, Toast.LENGTH_SHORT).show();
                                    }

                                    super.onResponse(call, response);
                                }
                            });
                }

                break;

            case R.id.user_icon:
                ApiHelper.getUserById(mSelectedUserId, new MyCallback<UserBase>() {
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

    /**
     * The method calls when logged user add this user as friend
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(UserUpdateEvent event) {
        EventBus.getDefault().post(new FriendsFragment.FriendsUpdateEvent());
        setInformationAboutUser();
    }

    /**
     * The method sets information about user
     */
    private void setInformationAboutUser() {
        ApiHelper.getUserWithState(mSelectedUserId, getSharedPrefHelper().getLoggedUserId(),
                new MyCallback<UserBaseWithState>() {
                    @Override
                    public void onResponse(Call<UserBaseWithState> call, Response<UserBaseWithState> response) {
                        if (response.isSuccessful()) {

                            IconUtils.setUserIcon(response.body().getUser(), mUserIcon);
                            mTextViewFirstAndLastName.setText(response.body().getUser().getFirstAndLastName());
                            mTextViewGender.setText(response.body().getUser().getGender().getStringRes());
                            mTextViewAge.setText(response.body().getUser().getAge(UserProfileActivity.this));

                            if (response.body().getUser().isOnline()) {
                                mImageViewOnlineStatus.setVisibility(View.VISIBLE);
                                mTextViewOnlineStatus.setText(R.string.text_online);
                            } else {
                                mImageViewOnlineStatus.setVisibility(View.GONE);
                                mTextViewOnlineStatus.setText(R.string.text_offline);
                            }

                            mButtonFriend.setEnabled(true);
                            mButtonMessage.setEnabled(true);
                            mButtonRemoveRequest.setVisibility(View.GONE);

                            if (!response.body().getUser().isDeleted()) {

                                mButtonFriend.setVisibility(View.VISIBLE);
                                mButtonMessage.setVisibility(View.VISIBLE);

                                switch (response.body().getState()) {
                                    case FRIEND:
                                        mButtonFriend.setText(getString(R.string.text_remove_from_friends));
                                        mButtonMessage.setEnabled(true);
                                        break;

                                    case CURRENT_USER:
                                        mButtonFriend.setVisibility(View.GONE);
                                        mButtonMessage.setVisibility(View.GONE);
                                        break;

                                    case UNRELATED_USER:
                                        mButtonFriend.setText(getString(R.string.text_add_as_a_friend));

                                        if (response.body().getUser().isReceiveOnlyFriendsMessages()) {
                                            mButtonMessage.setEnabled(false);
                                        } else {
                                            mButtonMessage.setEnabled(true);
                                        }
                                        break;

                                    case REQUESTED_FOR_FRIENDSHIP:
                                        mButtonFriend.setText(getString(R.string.text_remove_request));

                                        if (response.body().getUser().isReceiveOnlyFriendsMessages()) {
                                            mButtonMessage.setEnabled(false);
                                        } else {
                                            mButtonMessage.setEnabled(true);
                                        }
                                        break;

                                    case REQUESTER_OF_FRIENDSHIP:
                                        mButtonRemoveRequest.setVisibility(View.VISIBLE);
                                        mButtonFriend.setText(getString(R.string.text_accept_request));

                                        if (response.body().getUser().isReceiveOnlyFriendsMessages()) {
                                            mButtonMessage.setEnabled(false);
                                        } else {
                                            mButtonMessage.setEnabled(true);
                                        }
                                        break;
                                }
                            } else {
                                mButtonFriend.setVisibility(View.GONE);
                                mButtonMessage.setVisibility(View.GONE);
                            }
                        }
                        super.onResponse(call, response);
                    }
                });

        ApiHelper.calculateDistance(getSharedPrefHelper().getLoggedUserId(), mSelectedUserId,
                new MyCallback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            mTextViewDistance.setText(String.valueOf(response.body()));
                        }

                        super.onResponse(call, response);
                    }
                });
    }

    /**
     * Event for updating user information
     */
    static class UserUpdateEvent {
    }
}
