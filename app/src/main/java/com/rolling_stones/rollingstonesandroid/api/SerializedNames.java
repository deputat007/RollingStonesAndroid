package com.rolling_stones.rollingstonesandroid.api;


public interface SerializedNames {

    interface UserBase {
        String ID = "Id";
        String FIRST_NAME = "FirstName";
        String LAST_NAME = "LastName";
        String LOGIN = "Login";
        String GENDER = "Gender";
        String DATE_OF_BIRTH = "Dob";
        String RECEIVE_ONLY_FRIENDS_MESSAGES = "ReceiveOnlyFriendsMessages";
        String PHOTO_URL = "PhotoUrl";
        String PHOTO_SMALL_URL = "PhotoSmallUrl";
        String IS_DELETED = "IsDeleted";
        String IS_ONLINE = "IsOnline";
    }

    interface User {
        String PASSWORD = "Password";
        @SuppressWarnings("unused")
        String SALT = "Salt";
    }

    interface Message {
        String ID = "Id";
        String TEXT = "Text";
        String SENDER = "Sender";
        String RECIPIENT = "Recipient";
        String DATE_AND_TIME = "DateAndTime";
        String GROUP = "Group";
    }

    @SuppressWarnings("unused")
    interface Group {
        String ID = "Id";
        String NAME = "Name";
        String MEMBERS = "Members";
        String PHOTO_URL = "PhotoUrl";
        String PHOTO_SMALL_URL = "PhotoSmallUrl";
    }

    interface UserBaseWithState {
        String USER = "User";
        String STATE = "State";
    }
}
