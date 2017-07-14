package com.rolling_stones.rollingstonesandroid.utils;


import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;


public class SoundNotificationHelper {

    public static void playSound(@NonNull final Context context) {
        if (MyApplication.getApplication().getSharedPrefHelper().isSoundEnabled()) {
            final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notification);

            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }
}
