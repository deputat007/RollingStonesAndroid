package com.rolling_stones.rollingstonesandroid.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonElement;
import com.rolling_stones.rollingstonesandroid.activity.ChatActivity;
import com.rolling_stones.rollingstonesandroid.activity.ChatGroupActivity;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.fragments.MessagesFragment;
import com.rolling_stones.rollingstonesandroid.fragments.RequestsFragment;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;
import com.rolling_stones.rollingstonesandroid.utils.NotificationHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignalRService extends Service {

    private final static String TAG = SignalRService.class.getSimpleName();

    private final static String QUERY_STRING = "token=";

    private final static String MESSAGES_HUB = "MessagesHub";
    private final static String BROAD_CAST_DIALOGUE_MESSAGE = "broadCastDialogueMessage";
    private final static String BROAD_CAST_GROUP_MESSAGE = "BroadCastGroupMessage";

    private final static String REQUESTS_HUB = "RequestsHub";
    private final static String BROAD_CAST_REQUEST = "BroadCastRequest";

    private HubConnection mHubConnection;
    private HubProxy mMessagesHubProxy;
    private HubProxy mRequestsHubProxy;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        final String token = MyApplication
                .getApplication()
                .getSharedPrefHelper()
                .getToken()
                .getAccessToken();

        mHubConnection = new HubConnection(ApiHelper.BASE_URL + "signalr", QUERY_STRING + token, false,
                new Logger() {
                    @Override
                    public void log(String message, LogLevel level) {
                        Log.i(TAG, "level(" + level + "), message(" + message + ")");
                    }
                });
        mMessagesHubProxy = mHubConnection.createHubProxy(MESSAGES_HUB);
        mRequestsHubProxy = mHubConnection.createHubProxy(REQUESTS_HUB);

        startSignalR();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHubConnection.disconnect();
    }

    private void startSignalR() {
        final SignalRConnectTask task = new SignalRConnectTask();
        task.execute();
    }

    private void subscribeFriendRequest() {
        mRequestsHubProxy.subscribe(BROAD_CAST_REQUEST).addReceivedHandler(new Action<JsonElement[]>() {
            @Override
            public void run(JsonElement[] obj) throws Exception {
                int userId = obj[0].getAsInt();

                ApiHelper.getUserById(userId, new Callback<UserBase>() {
                    @Override
                    public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                        if (response.isSuccessful()) {
                            if (MyApplication.getApplication().getSharedPrefHelper().isNotificationEnabled()) {
                                NotificationHelper.showRequestNotification(SignalRService.this, response.body());
                            }

                            EventBus.getDefault().post(new RequestsFragment.SignalRRequestEvent(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserBase> call, Throwable t) {
                    }
                });

            }
        });
    }

    private void subscribeDialogueMessage() {
        mMessagesHubProxy.subscribe(BROAD_CAST_DIALOGUE_MESSAGE).addReceivedHandler(new Action<JsonElement[]>() {
            @Override
            public void run(JsonElement[] obj) throws Exception {
                final int senderId = obj[0].getAsInt();
                final String text = obj[2].getAsString();
                final Date date = DateFormatter.parseString(obj[3].getAsString(),
                        DateFormatter.DEFAULT_PATTERN_WITH_TIME);
                int recipientId = obj[4].getAsInt();

                final Message message = new Message();
                message.setText(text);
                message.setDateAndTime(date);

                ApiHelper.getUserById(recipientId, new Callback<UserBase>() {
                    @Override
                    public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                        if (response.isSuccessful()) {
                            message.setRecipient(response.body());

                            ApiHelper.getUserById(senderId, new Callback<UserBase>() {
                                @Override
                                public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                                    if (response.isSuccessful()) {
                                        message.setSender(response.body());
                                        if (!EventBus.getDefault()
                                                .hasSubscriberForEvent(ChatActivity.SignalREvent.class) &&
                                                MyApplication.getApplication().getSharedPrefHelper()
                                                        .isNotificationEnabled()) {
                                            NotificationHelper
                                                    .showMessageNotification(SignalRService.this, message);
                                        }

                                        EventBus.getDefault()
                                                .post(new MessagesFragment.SignalREvent(message));
                                        EventBus.getDefault()
                                                .post(new ChatActivity.SignalREvent(message));
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserBase> call, Throwable t) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<UserBase> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void subscribeGroupMessage() {
        mMessagesHubProxy.subscribe(BROAD_CAST_GROUP_MESSAGE).addReceivedHandler(new Action<JsonElement[]>() {
            @Override
            public void run(JsonElement[] obj) throws Exception {
                final int senderId = obj[0].getAsInt();
                final String photoSmallUrl = obj[1].getAsString();
                final String text = obj[2].getAsString();
                final Date date = DateFormatter.parseString(obj[3].getAsString(),
                        DateFormatter.DEFAULT_PATTERN_WITH_TIME);
                final int groupId = obj[4].getAsInt();

                final Message message = new Message();
                message.setSender(new UserBase(senderId, photoSmallUrl));
                message.setText(text);
                message.setDateAndTime(date);

                ApiHelper.getUserById(senderId, new Callback<UserBase>() {
                    @Override
                    public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                        if (response.isSuccessful()) {
                            message.setSender(response.body());

                            ApiHelper.getGroupById(groupId, new Callback<Group>() {
                                @Override
                                public void onResponse(Call<Group> call, Response<Group> response) {
                                    if (response.isSuccessful()) {
                                        message.setGroup(response.body());

                                        if (!EventBus.getDefault()
                                                .hasSubscriberForEvent(ChatGroupActivity.SignalREvent.class) &&
                                                MyApplication.getApplication().getSharedPrefHelper()
                                                        .isNotificationEnabled()) {
                                            NotificationHelper
                                                    .showMessageNotification(SignalRService.this, message);
                                        }

                                        EventBus.getDefault().post(new ChatGroupActivity.SignalREvent(message));
                                        EventBus.getDefault().post(new MessagesFragment.SignalREvent(message));
                                    }
                                }

                                @Override
                                public void onFailure(Call<Group> call, Throwable t) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<UserBase> call, Throwable t) {

                    }
                });
            }
        });
    }

    private class SignalRConnectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            final ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
            final SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

            try {
                signalRFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            subscribeDialogueMessage();
            subscribeGroupMessage();
            subscribeFriendRequest();
        }
    }
}
