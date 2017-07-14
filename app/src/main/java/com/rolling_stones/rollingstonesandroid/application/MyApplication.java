package com.rolling_stones.rollingstonesandroid.application;

import android.app.Application;
import android.content.Intent;

import com.rolling_stones.rollingstonesandroid.activity.LoginActivity;
import com.rolling_stones.rollingstonesandroid.services.SignalRService;
import com.rolling_stones.rollingstonesandroid.utils.SharedPreferencesHelper;


public class MyApplication extends Application {

    private static MyApplication sInstance;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    public static synchronized MyApplication getApplication() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public SharedPreferencesHelper getSharedPrefHelper() {
        if (mSharedPreferencesHelper == null) {
            mSharedPreferencesHelper = new SharedPreferencesHelper(this);
        }

        return mSharedPreferencesHelper;
    }

    public void logout() {
        getSharedPrefHelper().removeLoggedUserId();
        getSharedPrefHelper().removeToken();
        stopService(new Intent(this, SignalRService.class));

        final Intent intent = LoginActivity.getIntent(this, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    public boolean isUserLogged() {
        return getSharedPrefHelper().isLoggedUser();
    }

    public int getLoggedUserId() {
        return getSharedPrefHelper().getLoggedUserId();
    }
}
