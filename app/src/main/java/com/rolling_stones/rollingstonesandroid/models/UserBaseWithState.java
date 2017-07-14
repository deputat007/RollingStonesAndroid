package com.rolling_stones.rollingstonesandroid.models;


import com.google.gson.annotations.SerializedName;

public class UserBaseWithState {

    @SerializedName("User")
    private UserBase mUser;
    @SerializedName("State")
    private UserState mState;

    public UserBaseWithState(UserBase user, UserState state) {
        mUser = user;
        mState = state;
    }

    public UserBaseWithState() {
    }

    public UserBase getUser() {
        return mUser;
    }

    public void setUser(UserBase user) {
        mUser = user;
    }

    public UserState getState() {
        return mState;
    }

    public void setState(UserState state) {
        mState = state;
    }

    @Override
    public String toString() {
        return "UserBaseWithState{" +
                "mUser=" + mUser +
                ", mState=" + mState +
                '}';
    }
}
