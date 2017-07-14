package com.rolling_stones.rollingstonesandroid.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.utils.AgeCalculator;

import java.util.Date;


public class UserBase {

    @SerializedName("Id")
    private int mId;
    @SerializedName("FirstName")
    private String mFirstName;
    @SerializedName("LastName")
    private String mLastName;
    @SerializedName("Login")
    private String mLogin;
    @SerializedName("Gender")
    private Gender mGender;
    @SerializedName("Dob")
    private Date mDateOfBirth;
    @SerializedName("ReceiveOnlyFriendsMessages")
    private boolean mReceiveOnlyFriendsMessages;
    @SerializedName("PhotoUrl")
    private String mPhotoUrl;
    @SerializedName("PhotoSmallUrl")
    private String mPhotoSmallUrl;
    @SerializedName("IsDeleted")
    private boolean mIsDeleted;
    @SerializedName("IsOnline")
    private boolean mIsOnline;

    public UserBase() {
    }

    public UserBase(int id) {
        mId = id;
    }

    public UserBase(int id, String firstName, String lastName, String login, Gender gender,
                    Date dateOfBirth, boolean receiveOnlyFriendsMessages) {
        this.mId = id;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mLogin = login;
        this.mGender = gender;
        this.mDateOfBirth = dateOfBirth;
        this.mReceiveOnlyFriendsMessages = receiveOnlyFriendsMessages;
    }

    public UserBase(String firstName, String lastName, String login, Gender gender,
                    Date dateOfBirth, boolean receiveOnlyFriendsMessages) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mLogin = login;
        this.mGender = gender;
        this.mDateOfBirth = dateOfBirth;
        this.mReceiveOnlyFriendsMessages = receiveOnlyFriendsMessages;
    }

    public UserBase(int id, String firstName, String lastName, String login, Gender gender,
                    Date dateOfBirth, boolean receiveOnlyFriendsMessages, String photoUrl,
                    String photoSmallUrl, boolean isDeleted) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mLogin = login;
        mGender = gender;
        mDateOfBirth = dateOfBirth;
        mReceiveOnlyFriendsMessages = receiveOnlyFriendsMessages;
        mPhotoUrl = photoUrl;
        mPhotoSmallUrl = photoSmallUrl;
        mIsDeleted = isDeleted;
    }

    public UserBase(int id, String firstName, String lastName, String login, Gender gender,
                    Date dateOfBirth, boolean receiveOnlyFriendsMessages, String photoUrl,
                    String photoSmallUrl, boolean isDeleted, boolean isOnline) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mLogin = login;
        mGender = gender;
        mDateOfBirth = dateOfBirth;
        mReceiveOnlyFriendsMessages = receiveOnlyFriendsMessages;
        mPhotoUrl = photoUrl;
        mPhotoSmallUrl = photoSmallUrl;
        mIsDeleted = isDeleted;
        mIsOnline = isOnline;
    }

    public UserBase(int senderId, String imagePath) {
        mId = senderId;
        mPhotoSmallUrl = imagePath;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        this.mLogin = login;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender gender) {
        this.mGender = gender;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.mDateOfBirth = dateOfBirth;
    }

    public boolean isReceiveOnlyFriendsMessages() {
        return mReceiveOnlyFriendsMessages;
    }

    public void setReceiveOnlyFriendsMessages(boolean receiveOnlyFriendsMessages) {
        this.mReceiveOnlyFriendsMessages = receiveOnlyFriendsMessages;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    @SuppressWarnings("unused")
    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public String getPhotoSmallUrl() {
        return mPhotoSmallUrl;
    }

    @SuppressWarnings("unused")
    public void setPhotoSmallUrl(String photoSmallUrl) {
        mPhotoSmallUrl = photoSmallUrl;
    }

    public boolean isDeleted() {
        return mIsDeleted;
    }

    @SuppressWarnings("unused")
    public void setDeleted(boolean deleted) {
        mIsDeleted = deleted;
    }

    @Override
    public String toString() {
        return "UserBase{" +
                "mId=" + mId +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mLogin='" + mLogin + '\'' +
                ", mGender=" + mGender +
                ", mDateOfBirth=" + mDateOfBirth +
                ", mReceiveOnlyFriendsMessages=" + mReceiveOnlyFriendsMessages +
                '}';
    }

    public boolean isOnline() {
        return mIsOnline;
    }

    public void setOnline(boolean online) {
        mIsOnline = online;
    }

    public String getFirstAndLastName() {
        return getFirstName() + " " + getLastName();
    }

    public String getAge(@NonNull final Context context) {
        return AgeCalculator.calculateAge(getDateOfBirth()).getYears() + " " +
                context.getString(R.string.text_years);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBase)) return false;

        UserBase userBase = (UserBase) o;

        return getId() == userBase.getId() &&
                isReceiveOnlyFriendsMessages() == userBase.isReceiveOnlyFriendsMessages() &&
                isDeleted() == userBase.isDeleted() &&
                isOnline() == userBase.isOnline() &&
                getFirstName().equals(userBase.getFirstName()) &&
                getLastName().equals(userBase.getLastName()) &&
                getLogin().equals(userBase.getLogin()) &&
                getGender() == userBase.getGender() &&
                getDateOfBirth().equals(userBase.getDateOfBirth()) &&
                (getPhotoUrl() != null ? getPhotoUrl().equals(userBase.getPhotoUrl()) :
                        userBase.getPhotoUrl() == null && (getPhotoSmallUrl() != null ?
                                getPhotoSmallUrl().equals(userBase.getPhotoSmallUrl()) :
                                userBase.getPhotoSmallUrl() == null));

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        result = 31 * result + getLogin().hashCode();
        result = 31 * result + getGender().hashCode();
        result = 31 * result + getDateOfBirth().hashCode();
        result = 31 * result + (isReceiveOnlyFriendsMessages() ? 1 : 0);
        result = 31 * result + (getPhotoUrl() != null ? getPhotoUrl().hashCode() : 0);
        result = 31 * result + (getPhotoSmallUrl() != null ? getPhotoSmallUrl().hashCode() : 0);
        result = 31 * result + (isDeleted() ? 1 : 0);
        result = 31 * result + (isOnline() ? 1 : 0);
        return result;
    }
}
