package com.rolling_stones.rollingstonesandroid.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

public class GlideLoader {

    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @NonNull String pathOrUri) {
        Glide.with(context)
                .load(pathOrUri)
                .asBitmap()
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }

    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @DrawableRes int res) {
        Glide.with(context)
                .load(res)
                .asBitmap()
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }

    @SuppressWarnings("unused")
    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @NonNull File file) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }

    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @NonNull Uri uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }
}
