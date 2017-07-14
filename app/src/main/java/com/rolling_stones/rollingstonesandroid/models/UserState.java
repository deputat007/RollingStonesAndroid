package com.rolling_stones.rollingstonesandroid.models;


public enum UserState {
    CURRENT_USER(0),            //indicates that the user is the current user, who requests the page
    REQUESTER_OF_FRIENDSHIP(1),  //indicates that the user has sent a friend request to the current user, who requests the page
    REQUESTED_FOR_FRIENDSHIP(2), //indicates that the user has a friend request that has been sent by the current user, who requests the page
    FRIEND(3),                 //indicates that the user is a friend of the current user, who requests the page
    UNRELATED_USER(4);          //indicates that the user is not related to the current user, who requests the page


    int state;

    UserState(int i) {
        state = i;
    }

    public int getState() {
        return state;
    }
}
