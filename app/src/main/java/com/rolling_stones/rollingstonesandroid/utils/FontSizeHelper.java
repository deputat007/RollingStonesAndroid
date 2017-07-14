package com.rolling_stones.rollingstonesandroid.utils;


import android.content.Context;
import android.support.annotation.NonNull;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;

public class FontSizeHelper {
    public static float getFontSize(@NonNull final Context context) {
        switch (MyApplication.getApplication().getSharedPrefHelper().getFontSize()) {
            case 0:
                return context.getResources().getDimension(R.dimen.text_size_12);
            default:
            case 1:
                return context.getResources().getDimension(R.dimen.text_size_16);
            case 2:
                return context.getResources().getDimension(R.dimen.text_size_20);
        }
    }
}
