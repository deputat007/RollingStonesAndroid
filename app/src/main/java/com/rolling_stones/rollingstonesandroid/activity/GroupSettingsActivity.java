package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.adapter.MembersAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.GlideLoader;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.SelectFriendDialogFragment;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Settings(Group chat)
 */
public class GroupSettingsActivity extends BaseActivity implements MembersAdapter.OnClickListener,
        View.OnClickListener {

    private CircleImageView mGroupIcon;
    private TextInputLayout mTextInputLayoutGroupTitle;
    private TextInputEditText mTextInputEditTextGroupTitle;
    private LinearLayout mWrapperAddUser;
    private LinearLayout mWrapperLeaveChat;
    private RecyclerViewEmptySupport mRecyclerViewMembers;

    private MembersAdapter mAdapter;
    private int mGroupId;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, GroupSettingsActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_group_settings;
    }

    @Override
    protected void initUI() {
        mGroupIcon = findViewWithId(R.id.group_icon);
        mTextInputLayoutGroupTitle = findViewWithId(R.id.text_input_layout_group_title);
        mTextInputEditTextGroupTitle = findViewWithId(R.id.text_input_edit_text_group_title);
        mWrapperAddUser = findViewWithId(R.id.wrapper_add_user);
        mWrapperLeaveChat = findViewWithId(R.id.wrapper_leave_chat);
        mRecyclerViewMembers = findViewWithId(R.id.recycler_view_users);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_activity_settings);

        mGroupId = getIntent().getIntExtra(Constants.KEY_SELECTED_GROUP_ID, -1);
        mAdapter = new MembersAdapter(this);
        mRecyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewMembers.setAdapter(mAdapter);
        mRecyclerViewMembers.setNestedScrollingEnabled(false);
        mWrapperLeaveChat.setOnClickListener(this);
        mWrapperAddUser.setOnClickListener(this);

        mTextInputEditTextGroupTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateGroupTitle()) {
                    saveChanges();
                }
            }
        });

        updateGroup();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void saveChanges() {
        ApiHelper.getGroupById(mGroupId, new MyCallback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    final Group group = response.body();
                    group.setName(mTextInputEditTextGroupTitle.getText().toString().trim());
                    ApiHelper.updateGroup(group, new MyCallback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                EventBus.getDefault().post(new ChatGroupActivity.SettingsUpdateEvent());
                            }

                            super.onResponse(call, response);
                        }
                    });
                }
                super.onResponse(call, response);
            }
        });
    }

    @Override
    public void onUserClicked(@NonNull View view, int position) {
        final Bundle args = new Bundle();
        args.putInt(Constants.KEY_SELECTED_USER_ID,
                mAdapter.getItem(position).getId());

        startActivity(UserProfileActivity.getIntent(GroupSettingsActivity.this, args));
    }

    @Override
    public void onDeleteClicked(@NonNull View view, final int position) {
        ApiHelper.removeUserFromGroup(mAdapter.getItem(position).getId(), mGroupId,
                new MyCallback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            mAdapter.remove(mAdapter.getItem(position));
                        }

                        super.onResponse(call, response);
                    }
                });
    }

    private void updateGroup() {
        ApiHelper.getGroupById(mGroupId, new MyCallback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    GlideLoader.loadImage(GroupSettingsActivity.this, mGroupIcon,
                            response.body().getPhotoUrl());
                    mTextInputEditTextGroupTitle.setText(response.body().getName());
                    mAdapter.replaceAll(response.body().getMembers());
                }

                super.onResponse(call, response);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wrapper_add_user:

                final DialogFragment dialogFragment = SelectFriendDialogFragment.newInstance(
                        SelectFriendDialogFragment.SINGLE_SELECTION_MODE);
                dialogFragment.show(getSupportFragmentManager(), Constants.TAG_DIALOG_SELECT_FRIENDS);
                break;

            case R.id.wrapper_leave_chat:
                leaveChat();
                break;
        }
    }

    /**
     * The method calls when user selected friends for adding to group {@link SelectFriendDialogFragment},
     * And adds this friends to group {@link ApiHelper#addUserToGroup(int, int, Callback)} and
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(SelectFriendEvent event) {
        final int[] selectedIds = event.ids;

        ApiHelper.addUserToGroup(selectedIds[0], mGroupId, new MyCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ApiHelper.getUserById(selectedIds[0], new MyCallback<UserBase>() {
                        @Override
                        public void onResponse(Call<UserBase> call, final Response<UserBase> response) {
                            if (response.isSuccessful()) {
                                mAdapter.add(response.body());
                            }

                            super.onResponse(call, response);
                        }
                    });
                }

                super.onResponse(call, response);
            }
        });
    }

    /**
     * The method calls {@link ApiHelper#removeUserFromGroup(int, int, Callback)} for logged user
     */
    private void leaveChat() {
        ApiHelper.removeUserFromGroup(getSharedPrefHelper().getLoggedUserId(), mGroupId,
                new MyCallback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(GroupSettingsActivity.this,
                                    R.string.text_you_left_this_chat, Toast.LENGTH_SHORT).show();

                            onBackPressed();
                        }

                        super.onResponse(call, response);
                    }
                });
    }

    /**
     * The method validates @{@link #mTextInputEditTextGroupTitle}
     *
     * @return true if data is valid
     */
    private boolean validateGroupTitle() {
        final String firstName = mTextInputEditTextGroupTitle.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            mTextInputLayoutGroupTitle.setError(getString(R.string.error_group_title_is_empty));
            requestFocus(mTextInputLayoutGroupTitle);
            return false;
        }

        mTextInputLayoutGroupTitle.setErrorEnabled(false);
        return true;
    }

    /**
     * Event for adding friends to group chat {@link SelectFriendDialogFragment}
     */
    public static class SelectFriendEvent {
        private final int[] ids;

        public SelectFriendEvent(int[] ids) {
            this.ids = ids;
        }
    }
}
