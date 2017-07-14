package com.rolling_stones.rollingstonesandroid.models;

import android.support.annotation.StringRes;

import com.rolling_stones.rollingstonesandroid.R;


public enum Gender {

    MALE("M", R.string.text_male), FEMALE("F", R.string.text_female);

    @StringRes
    private final int stringRes;
    private final String gender;

    Gender(String gender, @StringRes int stringRes) {
        this.gender = gender;
        this.stringRes = stringRes;
    }

    public String getGender() {
        return gender;
    }

    @StringRes
    public int getStringRes() {
        return stringRes;
    }
}
