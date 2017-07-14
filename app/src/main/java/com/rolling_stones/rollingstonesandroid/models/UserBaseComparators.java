package com.rolling_stones.rollingstonesandroid.models;


import java.util.Comparator;


public class UserBaseComparators {

    private static final Comparator<UserBase> FIRST_NAME_COMPARATOR_ASC = new Comparator<UserBase>() {
        @Override
        public int compare(UserBase o1, UserBase o2) {
            return o1.getFirstName().compareTo(o2.getFirstName());
        }
    };

    private static final Comparator<UserBase> LAST_NAME_COMPARATOR_ASC = new Comparator<UserBase>() {
        @Override
        public int compare(UserBase o1, UserBase o2) {
            return o1.getLastName().compareTo(o2.getLastName());
        }
    };

    public static Comparator<UserBase> getComparatorByType(int type) {
        switch (type) {
            default:
            case 0:
                return FIRST_NAME_COMPARATOR_ASC;

            case 1:
                return LAST_NAME_COMPARATOR_ASC;
        }
    }
}
