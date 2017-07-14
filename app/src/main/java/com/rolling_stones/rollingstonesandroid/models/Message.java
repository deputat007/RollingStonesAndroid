package com.rolling_stones.rollingstonesandroid.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class Message {

    @SerializedName("Id")
    private int mId;
    @SerializedName("Text")
    private String mText;
    @SerializedName("Sender")
    private UserBase mSender;
    @SerializedName("Recipient")
    private UserBase mRecipient;
    @SerializedName("DateAndTime")
    private Date mDateAndTime;
    @SerializedName("Group")
    private Group mGroup;

    public Message() {
    }

    public Message(int id, String text, UserBase sender, UserBase recipient, Date dateAndTime) {
        mId = id;
        mText = text;
        mSender = sender;
        mRecipient = recipient;
        mDateAndTime = dateAndTime;
    }

    public Message(int id, String text, UserBase sender, UserBase recipient, Date dateAndTime,
                   Group group) {
        mId = id;
        mText = text;
        mSender = sender;
        mRecipient = recipient;
        mDateAndTime = dateAndTime;
        mGroup = group;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public UserBase getSender() {
        return mSender;
    }

    @SuppressWarnings("unused")
    public void setSender(UserBase sender) {
        mSender = sender;
    }

    public UserBase getRecipient() {
        return mRecipient;
    }

    public void setRecipient(UserBase recipient) {
        mRecipient = recipient;
    }

    public Date getDateAndTime() {
        return mDateAndTime;
    }

    @SuppressWarnings("unused")
    public void setDateAndTime(Date dateAndTime) {
        mDateAndTime = dateAndTime;
    }

    public Group getGroup() {
        return mGroup;
    }

    public void setGroup(Group group) {
        mGroup = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        return getId() == message.getId() &&
                getText().equals(message.getText()) &&
                getSender().equals(message.getSender()) &&
                (getRecipient() != null ? getRecipient().equals(message.getRecipient()) :
                        message.getRecipient() == null && getDateAndTime().equals(message.getDateAndTime()) &&
                                (getGroup() != null ? getGroup().equals(message.getGroup()) : message.getGroup() == null));

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getText().hashCode();
        result = 31 * result + getSender().hashCode();
        result = 31 * result + (getRecipient() != null ? getRecipient().hashCode() : 0);
        result = 31 * result + getDateAndTime().hashCode();
        result = 31 * result + (getGroup() != null ? getGroup().hashCode() : 0);
        return result;
    }
}
