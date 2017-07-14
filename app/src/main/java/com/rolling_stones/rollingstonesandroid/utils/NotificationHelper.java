package com.rolling_stones.rollingstonesandroid.utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.ChatActivity;
import com.rolling_stones.rollingstonesandroid.activity.ChatGroupActivity;
import com.rolling_stones.rollingstonesandroid.activity.UserProfileActivity;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.UserBase;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    public static void showMessageNotification(@NonNull final Context context,
                                               @NonNull final Message message) {
        final NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (message.getGroup() == null) {
            final Bundle args = new Bundle();
            args.putInt(Constants.KEY_SELECTED_USER_ID, message.getSender().getId());

            final Intent resultIntent = ChatActivity.getIntent(context, args);
            final PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            final Notification notification = buildNotification(context,
                    message.getSender().getFirstAndLastName(), message.getText(),
                    resultPendingIntent);

            mNotifyMgr.notify(message.getSender().getId(), notification);
        } else {
            final Bundle args = new Bundle();
            args.putInt(Constants.KEY_SELECTED_GROUP_ID, message.getGroup().getId());

            final Intent resultIntent = ChatGroupActivity.getIntent(context, args);
            final PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            final Notification notification = buildNotification(context,
                    message.getGroup().getName(), message.getText(), resultPendingIntent);

            mNotifyMgr.notify(message.getGroup().getId(), notification);

        }
    }

    private static Notification buildNotification(@NonNull final Context context,
                                                  @NonNull final String title,
                                                  @NonNull final String text,
                                                  @NonNull final PendingIntent contentIntent) {

        final Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setColor(NotificationCompat.COLOR_DEFAULT)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build();

        notification.sound = Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.notification);

        return notification;
    }

    public static void showRequestNotification(Context context, UserBase user) {
        final NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        final Bundle args = new Bundle();
        args.putInt(Constants.KEY_SELECTED_USER_ID, user.getId());

        final Intent resultIntent = UserProfileActivity.getIntent(context, args);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        final Notification notification = buildNotification(context,
                user.getFirstAndLastName(), context.getString(R.string.text_sent_you_friend_request),
                resultPendingIntent);

        mNotifyMgr.notify(user.getId(), notification);
    }
}
