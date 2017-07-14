package com.rolling_stones.rollingstonesandroid.utils;


import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;

import com.rolling_stones.rollingstonesandroid.application.MyApplication;


public class VibratorServiceHelper {

    public static final long DEFAULT_DURATION = 500;

    public static void vibrate(@NonNull final Context context, long duration) {
        if (MyApplication.getApplication().getSharedPrefHelper().isVibrationEnabled()) {
            final Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(duration);
        }
    }
}
