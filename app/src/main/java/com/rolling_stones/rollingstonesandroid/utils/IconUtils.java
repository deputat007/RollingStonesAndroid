package com.rolling_stones.rollingstonesandroid.utils;


import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.UserBase;


public class IconUtils {

    private final static String BASE_URL = ApiHelper.BASE_URL + "Images/";

    private final static String URL_USERS = BASE_URL + "Users/";
    private final static String URL_GROUPS = BASE_URL + "Groups/";

    private final static String DEFAULT_USER = URL_USERS + "default.jpg";
    private final static String DEFAULT_USER_SMALL = URL_USERS + "default_small.jpg";

    private final static String DELETED = URL_USERS + "deleted.jpg";
    private final static String DELETED_SMALL = URL_USERS + "deleted_small.jpg";

    private final static String DEFAULT_GROUP_SMALL = URL_GROUPS + "default_small.jpg";
    @SuppressWarnings("unused")
    private final static String DEFAULT_GROUP = URL_GROUPS + "default.jpg";

    public static void setUserIconSmall(@NonNull final UserBase user,
                                        @NonNull final ImageView userIcon) {
        if (!user.isDeleted()) {
            if (user.getPhotoSmallUrl() != null) {
                GlideLoader.loadImage(userIcon.getContext(), userIcon, user.getPhotoSmallUrl());
            } else {
                GlideLoader.loadImage(userIcon.getContext(), userIcon, DEFAULT_USER_SMALL);
            }
        } else {
            GlideLoader.loadImage(userIcon.getContext(), userIcon, DELETED_SMALL);
        }
    }

    public static void setUserIcon(@NonNull final UserBase user,
                                   @NonNull final ImageView userIcon) {
        if (!user.isDeleted()) {
            if (user.getPhotoUrl() != null) {
                GlideLoader.loadImage(userIcon.getContext(), userIcon, user.getPhotoSmallUrl());
            } else {
                GlideLoader.loadImage(userIcon.getContext(), userIcon, DEFAULT_USER);
            }
        } else {
            GlideLoader.loadImage(userIcon.getContext(), userIcon, DELETED);
        }
    }

    public static void setGroupIconSmall(@NonNull final Group group, @NonNull final ImageView userIcon) {
        if (group.getPhotoSmallUrl() != null) {
            GlideLoader.loadImage(userIcon.getContext(), userIcon, group.getPhotoSmallUrl());
        } else {
            GlideLoader.loadImage(userIcon.getContext(), userIcon, DEFAULT_GROUP_SMALL);
        }
    }
}
