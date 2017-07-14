package com.rolling_stones.rollingstonesandroid.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.rolling_stones.rollingstonesandroid.models.Token;


public class SharedPreferencesHelper {

    private interface Constants {
        String FILE_NAME = "rolling_stones";
        String KEY_LOGGED_USER_ID = "key_logged_user_id";
        String KEY_TOKEN_WEB_API = "key_token_web_api";
        String KEY_URL_WEB_API = "key_url_web_api";

        String KEY_FONT_SIZE = "key_font_size";
        String KEY_LIST_ORDER = "key_list_order";
        String KEY_SOUND = "key_sound";
        String KEY_VIBRATION = "key_vibration";
        String KEY_NOTIFICATION = "key_notification";

        int DEFAULT_FONT_SIZE = 1;
        int DEFAULT_LIST_ORDER = 0;
        boolean DEFAULT_SOUND = true;
        boolean DEFAULT_VIBRATION = true;
        boolean DEFAULT_NOTIFICATION = true;
        String DEFAULT_URL = "http://192.168.1.148/rs-webapi/";
    }

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesHelper(@NonNull final Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void saveLoggedUserId(int id) {
        mEditor.putInt(Constants.KEY_LOGGED_USER_ID, id);

        saveChanges();
    }

    public int getLoggedUserId() {
        return mSharedPreferences.getInt(Constants.KEY_LOGGED_USER_ID, -1);
    }

    public boolean isLoggedUser() {
        return mSharedPreferences.contains(Constants.KEY_LOGGED_USER_ID);
    }

    public void saveToken(@NonNull final Token token) {
        mEditor.putString(Constants.KEY_TOKEN_WEB_API, new Gson().toJson(token));

        saveChanges();
    }

    public Token getToken() {
        return new Gson().fromJson(mSharedPreferences.getString(Constants.KEY_TOKEN_WEB_API, null),
                Token.class);
    }

    public void removeToken() {
        mEditor.remove(Constants.KEY_TOKEN_WEB_API);

        saveChanges();
    }

    public void saveUrl(@NonNull final String url) {
        mEditor.putString(Constants.KEY_URL_WEB_API, url);

        saveChanges();
    }

    public String getUrl() {
        return mSharedPreferences.getString(Constants.KEY_URL_WEB_API, Constants.DEFAULT_URL);
    }

    public void saveFontSize(int type) {
        mEditor.putInt(Constants.KEY_FONT_SIZE, type);

        saveChanges();
    }

    public int getFontSize() {
        return mSharedPreferences.getInt(Constants.KEY_FONT_SIZE, Constants.DEFAULT_FONT_SIZE);
    }

    public void saveSound(boolean isEnabled) {
        mEditor.putBoolean(Constants.KEY_SOUND, isEnabled);

        saveChanges();
    }

    public boolean isSoundEnabled() {
        return mSharedPreferences.getBoolean(Constants.KEY_SOUND, Constants.DEFAULT_SOUND);
    }

    public void saveVibration(boolean isEnabled) {
        mEditor.putBoolean(Constants.KEY_VIBRATION, isEnabled);

        saveChanges();
    }

    public boolean isVibrationEnabled() {
        return mSharedPreferences.getBoolean(Constants.KEY_VIBRATION, Constants.DEFAULT_VIBRATION);
    }

    public void saveNotification(boolean isEnabled) {
        mEditor.putBoolean(Constants.KEY_NOTIFICATION, isEnabled);

        saveChanges();
    }

    public boolean isNotificationEnabled() {
        return mSharedPreferences.getBoolean(Constants.KEY_NOTIFICATION, Constants.DEFAULT_NOTIFICATION);
    }

    public void saveListOrder(int type) {
        mEditor.putInt(Constants.KEY_LIST_ORDER, type);

        saveChanges();
    }

    public int getListOrder() {
        return mSharedPreferences.getInt(Constants.KEY_LIST_ORDER, Constants.DEFAULT_LIST_ORDER);
    }

    public void removeLoggedUserId() {
        mEditor.remove(Constants.KEY_LOGGED_USER_ID);

        saveChanges();
    }

    private void saveChanges() {
        mEditor.commit();
    }
}
