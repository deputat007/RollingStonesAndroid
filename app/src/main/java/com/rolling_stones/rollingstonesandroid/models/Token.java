package com.rolling_stones.rollingstonesandroid.models;


import com.google.gson.annotations.SerializedName;

import java.util.Locale;


public class Token {

    @SerializedName("access_token")
    private String mAccessToken;
    @SerializedName("token_type")
    private String mTokenType;
    @SerializedName("expires_in")
    private int mExpiresIn;

    public Token(String accessToken, String tokenType, int expiresIn) {
        mAccessToken = accessToken;
        mTokenType = tokenType;
        mExpiresIn = expiresIn;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    @SuppressWarnings("unused")
    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    private String getTokenType() {
        return mTokenType;
    }

    @SuppressWarnings("unused")
    public void setTokenType(String tokenType) {
        mTokenType = tokenType;
    }

    @SuppressWarnings("unused")
    public int getExpiresIn() {
        return mExpiresIn;
    }

    @SuppressWarnings("unused")
    public void setExpiresIn(int expiresIn) {
        mExpiresIn = expiresIn;
    }

    public String getFormattedToken() {
        return String.format(Locale.getDefault(), "%s %s", getTokenType(), getAccessToken());
    }
}
