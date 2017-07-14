package com.rolling_stones.rollingstonesandroid.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("deprecation")
public final class DateFormatter {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm";
    public static final String DAY_PATTERN = "dd MMM";

    public static final String DEFAULT_PATTERN_WITH_TIME = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String DEFAULT_PATTERN = "dd.MM.yyyy";
    public static final String DATE_WITH_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    public static String parseDate(@NonNull Date date, @NonNull Context context) {
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(DEFAULT_PATTERN, current).format(date);
    }

    public static String parseDate(@NonNull Date date, @NonNull Context context,
                                   @NonNull String pattern) {
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(pattern, current).format(date);
    }

    public static Date parseString(@NonNull String date, @NonNull Context context) {
        final Locale current = context.getResources().getConfiguration().locale;

        try {
            return new SimpleDateFormat(DEFAULT_PATTERN, current).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Date parseString(@NonNull String date, @NonNull String pattern) {

        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String parseDate(@Nullable Date date, @NonNull String pattern) {
        if (date == null){
            return null;
        }

        return new SimpleDateFormat(pattern).format(date);
    }

    @SuppressWarnings("unused")
    public static DateFormat getDateFormatter(@NonNull Context context) {
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(DATE_FORMAT_PATTERN, current);
    }

    @SuppressWarnings("unused")
    public static DateFormat getDateFormatter(@NonNull Context context, @NonNull String pattern) {
        final Locale current = context.getResources().getConfiguration().locale;

        return new SimpleDateFormat(pattern, current);
    }
}
