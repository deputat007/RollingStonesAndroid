package com.rolling_stones.rollingstonesandroid.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class User extends UserBase {

    @SerializedName("Password")
    private String mPassword;
    @SerializedName("Salt")
    private String mSalt;

    public User() {
    }

    public User(int id, String firstName, String lastName, String login, Gender gender,
                Date dateOfBirth, boolean receiveOnlyFriendsMessages, String password) {
        super(id, firstName, lastName, login, gender, dateOfBirth, receiveOnlyFriendsMessages);
        this.mPassword = password;
    }

    public User(String firstName, String lastName, String login, Gender gender, Date dateOfBirth,
                boolean receiveOnlyFriendsMessages, String password) {
        super(firstName, lastName, login, gender, dateOfBirth, receiveOnlyFriendsMessages);
        this.mPassword = password;
    }

    public User(int id, String firstName, String lastName, String login, Gender gender, Date dateOfBirth,
                boolean receiveOnlyFriendsMessages, String photoUrl, String photoSmallUrl,
                boolean isDeleted, String password) {
        super(id, firstName, lastName, login, gender, dateOfBirth, receiveOnlyFriendsMessages,
                photoUrl, photoSmallUrl, isDeleted);
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    @SuppressWarnings("unused")
    public void setPassword(String password) {
        this.mPassword = password;
    }

    private String getSalt() {
        return mSalt;
    }

    @SuppressWarnings("unused")
    public void setSalt(String salt) {
        mSalt = salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        return getPassword().equals(user.getPassword()) && getSalt().equals(user.getSalt());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + getSalt().hashCode();
        return result;
    }
}
