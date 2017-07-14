package com.rolling_stones.rollingstonesandroid.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.fragments.FriendsFragment;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.ChangePasswordDialogFragment;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.DeleteAccountDialogFragment;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.FontSizeDialogFragment;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.FriendListOrderDialogFragment;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.LogoutDialogFragment;
import com.rolling_stones.rollingstonesandroid.views.SettingsItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Response;


public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private SettingsItem mSettingsItemChangePassword;
    private SettingsItem mSettingsItemReceiveMessages;
    private SettingsItem mSettingsItemDeleteAccount;
    private SettingsItem mSettingsItemFriendListOrder;
    private SettingsItem mSettingsItemFontSize;
    private SettingsItem mSettingsItemSound;
    private SettingsItem mSettingsItemVibration;
    private SettingsItem mSettingsItemNotification;
    private SettingsItem mSettingsItemLogout;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, SettingsActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initUI() {
        mSettingsItemChangePassword = findViewWithId(R.id.si_change_password);
        mSettingsItemDeleteAccount = findViewWithId(R.id.si_delete_account);
        mSettingsItemFontSize = findViewWithId(R.id.si_font_size);
        mSettingsItemFriendListOrder = findViewWithId(R.id.si_friend_list_order);
        mSettingsItemReceiveMessages = findViewWithId(R.id.si_receive_messages);
        mSettingsItemSound = findViewWithId(R.id.si_sound);
        mSettingsItemVibration = findViewWithId(R.id.si_vibration);
        mSettingsItemNotification = findViewWithId(R.id.si_notification);
        mSettingsItemLogout = findViewWithId(R.id.si_logout);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        setTitle(R.string.title_activity_settings);
        setDisplayHomeAsUpEnabled(true);
        mSettingsItemSound.setSwitchChecked(getSharedPrefHelper().isSoundEnabled());
        mSettingsItemVibration.setSwitchChecked(getSharedPrefHelper().isVibrationEnabled());
        mSettingsItemNotification.setSwitchChecked(getSharedPrefHelper().isNotificationEnabled());
        mSettingsItemChangePassword.setOnClickListener(this);
        mSettingsItemReceiveMessages.setOnClickListener(this);
        mSettingsItemFriendListOrder.setOnClickListener(this);
        mSettingsItemDeleteAccount.setOnClickListener(this);
        mSettingsItemFontSize.setOnClickListener(this);
        mSettingsItemSound.setOnClickListener(this);
        mSettingsItemVibration.setOnClickListener(this);
        mSettingsItemNotification.setOnClickListener(this);
        mSettingsItemLogout.setOnClickListener(this);

        mSettingsItemReceiveMessages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(),
                        new MyCallback<UserBase>() {
                            @Override
                            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    final UserBase userBase = response.body();

                                    userBase.setReceiveOnlyFriendsMessages(isChecked);

                                    ApiHelper.updateUser(userBase, new MyCallback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (!response.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this,
                                                        getString(R.string.error_some_problem),
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
        });
        mSettingsItemSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPrefHelper().saveSound(isChecked);
            }
        });
        mSettingsItemVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPrefHelper().saveVibration(isChecked);
            }
        });
        mSettingsItemNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPrefHelper().saveNotification(isChecked);
            }
        });

        ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(), new MyCallback<UserBase>() {
            @Override
            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mSettingsItemReceiveMessages.setSwitchChecked(
                            response.body().isReceiveOnlyFriendsMessages());
                }
                super.onResponse(call, response);
            }
        });

        mSettingsItemFontSize.setSubtitleText(getResources()
                .getStringArray(R.array.font_size)[getSharedPrefHelper().getFontSize()]);

        mSettingsItemFriendListOrder.setSubtitleText(getResources()
                .getStringArray(R.array.list_order)[getSharedPrefHelper().getListOrder()]);
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
            case R.id.si_change_password:
                final DialogFragment dialogFragment = ChangePasswordDialogFragment.newInstance();
                dialogFragment.show(getSupportFragmentManager(), Constants.TAG_DIALOG_CHANGE_PASSWORD);
                break;

            case R.id.si_receive_messages:
                mSettingsItemReceiveMessages.setSwitchChecked(!mSettingsItemReceiveMessages.isSwitchChecked());
                break;

            case R.id.si_sound:
                mSettingsItemSound.setSwitchChecked(!mSettingsItemSound.isSwitchChecked());
                break;

            case R.id.si_vibration:
                mSettingsItemVibration.setSwitchChecked(!mSettingsItemVibration.isSwitchChecked());
                break;

            case R.id.si_notification:
                mSettingsItemNotification.setSwitchChecked(!mSettingsItemNotification.isSwitchChecked());
                break;

            case R.id.si_delete_account:
                final DialogFragment dialogFragmentDeleteAccount = DeleteAccountDialogFragment.newInstance();
                dialogFragmentDeleteAccount.show(getSupportFragmentManager(),
                        Constants.TAG_DIALOG_DELETE_ACCOUNT);
                break;

            case R.id.si_friend_list_order:
                final DialogFragment dialogFragmentFriendListOrder =
                        FriendListOrderDialogFragment.newInstance();
                dialogFragmentFriendListOrder.show(getSupportFragmentManager(),
                        Constants.TAG_DIALOG_LIST_ORDER);
                break;

            case R.id.si_font_size:
                final DialogFragment dialogFragmentFontSize = FontSizeDialogFragment.newInstance();
                dialogFragmentFontSize.show(getSupportFragmentManager(), Constants.TAG_DIALOG_FONT_STYLE);
                break;

            case R.id.si_logout:
                final DialogFragment dialogFragmentLogout = LogoutDialogFragment.newInstance();
                dialogFragmentLogout.show(getSupportFragmentManager(), Constants.TAG_DIALOG_LOGOUT);
                break;
        }
    }

    /**
     * The method is calling when font size changed {@link FontSizeDialogFragment}
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onFontSizeEvent(FontSizeEvent event) {
        mSettingsItemFontSize.setSubtitleText(getResources()
                .getStringArray(R.array.font_size)[getSharedPrefHelper().getFontSize()]);
    }

    /**
     * The method is calling when list order changed {@link FriendListOrderEvent}
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onListOrderEvent(FriendListOrderEvent event) {
        mSettingsItemFriendListOrder.setSubtitleText(getResources()
                .getStringArray(R.array.list_order)[getSharedPrefHelper().getListOrder()]);
        EventBus.getDefault().post(new FriendsFragment.SettingsUpdateEvent());
    }

    /**
     * The method is calling when user deleted account {@link DeleteAccountEvent}
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onDeleteEvent(DeleteAccountEvent event) {
        ApiHelper.deleteUser(getSharedPrefHelper().getLoggedUserId(), new MyCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    startActivity(AccountDeletedActivity
                            .getIntent(SettingsActivity.this, null)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }

                super.onResponse(call, response);
            }
        });
    }

    /**
     * The method is calling when user logout {@link LogoutDialogFragment}
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onLogoutEvent(MainActivity.LogoutEvent event) {
        MyApplication.getApplication().logout();
    }

    /**
     * Event for {@link DeleteAccountDialogFragment}
     */
    public static class DeleteAccountEvent {
    }

    /**
     * Event for {@link FontSizeDialogFragment}
     */
    public static class FontSizeEvent {
    }

    /**
     * Event for {@link FriendListOrderDialogFragment}
     */
    public static class FriendListOrderEvent {
    }
}
