package com.rolling_stones.rollingstonesandroid.utils;


import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;


public class AgeCalculator {

    @NonNull
    public static Age calculateAge(@NonNull final Date birthDate) {
        int years;
        int months;
        int days;

        final Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        final long currentTime = System.currentTimeMillis();
        final Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);

        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        months = currMonth - birthMonth;

        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;

            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                months--;
            }
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }

        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE)) {
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        } else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            final int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;

            if (months == 12) {
                years++;
                months = 0;
            }
        }

        return new Age(days, months, years);
    }

    public static class Age {
        private int mDays;
        private int mMonths;
        private int mYears;

        Age(int days, int months, int years) {
            mDays = days;
            mMonths = months;
            mYears = years;
        }

        @SuppressWarnings("unused")
        public int getDays() {
            return mDays;
        }

        @SuppressWarnings("unused")
        public int getMonths() {
            return mMonths;
        }

        public int getYears() {
            return mYears;
        }
    }
}
