package com.rolling_stones.rollingstonesandroid.utils;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;


public class DateHelper {

    public static boolean isSameDay(@NonNull final Date date, @NonNull final Date date2) {

        final Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(date.getTime());
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(date2.getTime());

        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
