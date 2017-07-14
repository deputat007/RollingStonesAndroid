package com.rolling_stones.rollingstonesandroid.api;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rolling_stones.rollingstonesandroid.BuildConfig;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.Gender;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.Token;
import com.rolling_stones.rollingstonesandroid.models.User;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.models.UserBaseWithState;
import com.rolling_stones.rollingstonesandroid.utils.deserializers.GroupDeserializer;
import com.rolling_stones.rollingstonesandroid.utils.deserializers.MessageDeserializer;
import com.rolling_stones.rollingstonesandroid.utils.deserializers.UserBaseDeserializer;
import com.rolling_stones.rollingstonesandroid.utils.deserializers.UserBaseWithStateDeserializer;
import com.rolling_stones.rollingstonesandroid.utils.deserializers.UserDeserializer;
import com.rolling_stones.rollingstonesandroid.utils.serializers.GroupSerializer;
import com.rolling_stones.rollingstonesandroid.utils.serializers.MessageSerializer;
import com.rolling_stones.rollingstonesandroid.utils.serializers.UserBaseSerializer;
import com.rolling_stones.rollingstonesandroid.utils.serializers.UserSerializer;

import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiHelper {

    //    public static final String BASE_URL = "http://172.20.14.73/rs-webapi/";
    public static final String BASE_URL;
//    public static final String BASE_URL = "http://172.20.8.204/rs-webapi/";
//    public static final String BASE_URL = "http:///rs-webapi/";
//    public static final String BASE_URL = "http:///rs-webapi/";

    private static final Retrofit sRetrofit;

    private static final AuthorizationService sAuthorizationService;
    private static final FriendsRequestsService sFriendsRequestService;
    private static final FriendsService sFriendsService;
    private static final GroupService sGroupService;
    private static final MessageService sMessageService;
    private static final UserDistanceCalculatorService sUserDistanceCalculatorService;
    private static final UserGroupsService sUserGroupService;
    private static final UserService sUserService;

    static {
        BASE_URL = MyApplication.getApplication().getSharedPrefHelper().getUrl();

        final OkHttpClient.Builder okClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okClient.addInterceptor(logging);
        }

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(UserBase.class, new UserBaseDeserializer())
                .registerTypeAdapter(UserBase.class, new UserBaseSerializer())
                .registerTypeAdapter(User.class, new UserDeserializer())
                .registerTypeAdapter(User.class, new UserSerializer())
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                .registerTypeAdapter(Message.class, new MessageSerializer())
                .registerTypeAdapter(Group.class, new GroupDeserializer())
                .registerTypeAdapter(Group.class, new GroupSerializer())
                .registerTypeAdapter(UserBaseWithState.class, new UserBaseWithStateDeserializer())
                .create();

        sRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okClient.build())
                .build();

        sAuthorizationService = sRetrofit.create(AuthorizationService.class);
        sFriendsRequestService = sRetrofit.create(FriendsRequestsService.class);
        sFriendsService = sRetrofit.create(FriendsService.class);
        sGroupService = sRetrofit.create(GroupService.class);
        sMessageService = sRetrofit.create(MessageService.class);
        sUserDistanceCalculatorService = sRetrofit.create(UserDistanceCalculatorService.class);
        sUserGroupService = sRetrofit.create(UserGroupsService.class);
        sUserService = sRetrofit.create(UserService.class);
    }

    // USER_SERVICE METHODS:
    public static void createUser(@NonNull final User user,
                                  @NonNull final Callback<Void> callback) {
        sUserService.createUser(user).enqueue(callback);
    }

    public static void getUserById(int userId, @NonNull final Callback<UserBase> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserService.getUserById(token.getFormattedToken(), userId).enqueue(callback);
    }

    public static void getUserWithState(int userId, int currentUserId,
                                        @NonNull final Callback<UserBaseWithState> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserService.getUserWithState(token.getFormattedToken(), userId, currentUserId)
                .enqueue(callback);
    }

    public static void updateUser(@NonNull final UserBase user,
                                  @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserService.updateUser(token.getFormattedToken(), user).enqueue(callback);
    }

    public static void findUsers(@NonNull final String text,
                                 @Nullable final Gender gender, int age, boolean onlyOnline,
                                 @NonNull final Callback<List<UserBase>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserService.findUsers(token.getFormattedToken(), text,
                gender == null ? null : gender.getGender(), age == 0 ? null : age, onlyOnline)
                .enqueue(callback);
    }

    public static void updateUserPassword(int userId, @NonNull final String oldPassword,
                                          @NonNull final String newPassword,
                                          @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();
        final String body = String.format(Locale.getDefault(),
                UserService.UPDATE_USER_PASSWORD_FORMAT, userId, oldPassword, newPassword);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);

        sUserService.updateUserPassword(token.getFormattedToken(), requestBody).enqueue(callback);
    }

    public static void deleteUser(int userId, @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();
        final String body = String.format(Locale.getDefault(), UserService.DELETE_USER_FORMAT, userId);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);

        sUserService.deleteUser(token.getFormattedToken(), requestBody).enqueue(callback);
    }

    public static void recoverUser(int userId, @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserService.recoverUser(token.getFormattedToken(), userId).enqueue(callback);
    }

    // AUTHORIZATION_SERVICE METHODS:
    public static void getToken(@NonNull final String login, @NonNull final String password,
                                @NonNull final Callback<Token> callback) {
        final String body = String.format(Locale.getDefault(),
                AuthorizationService.USER_TOKEN_FORMAT, login, password);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);

        sAuthorizationService.getToken(requestBody).enqueue(callback);
    }

    // FRIEND_SERVICE METHODS:
    public static void getFriends(int loggedUserId,
                                  @NonNull final Callback<List<UserBase>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsService.getFriends(token.getFormattedToken(), loggedUserId).enqueue(callback);
    }

    public static void addFriends(int loggedUserId, int friendId,
                                  @NonNull final Callback<Void> callback) {
        final String body = String.format(Locale.getDefault(), FriendsService.ADD_FRIENDS_FORMAT,
                loggedUserId, friendId);
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);

        sFriendsService.addFriends(token.getFormattedToken(), requestBody).enqueue(callback);
    }

    public static void removeFriends(int loggedUserId, int friendId,
                                     @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsService.removeFriends(token.getFormattedToken(), loggedUserId, friendId)
                .enqueue(callback);
    }

    @SuppressWarnings("unused")
    public static void areFriends(int loggedUserId, int friendId,
                                  @NonNull final Callback<Boolean> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsService.areFriends(token.getFormattedToken(), loggedUserId, friendId)
                .enqueue(callback);
    }

    public static void findFriends(int loggedUserId, @NonNull final String searchedString,
                                   @Nullable final Gender gender, int age, boolean onlyOnline,
                                   @NonNull final Callback<List<UserBase>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsService.findFriends(token.getFormattedToken(), loggedUserId, searchedString,
                gender == null ? null : gender.getGender(), age == 0 ? null : age, onlyOnline)
                .enqueue(callback);
    }

    // USER_DISTANCE_CALCULATOR_SERVICE METHODS:
    public static void calculateDistance(int userId1, int userId2,
                                         @NonNull final Callback<Integer> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserDistanceCalculatorService.calculateDistance(token.getFormattedToken(), userId1, userId2)
                .enqueue(callback);
    }

    // MESSAGES_SERVICE METHODS:
    public static void getLastMessages(int loggedUserId,
                                       @NonNull final Callback<List<Message>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sMessageService.getLastMessages(token.getFormattedToken(), loggedUserId).enqueue(callback);
    }

    public static void getLastMessagesForGroup(int groupId,
                                               @NonNull final Callback<List<Message>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sMessageService.getLastMessagesForGroup(token.getFormattedToken(), groupId).enqueue(callback);
    }

    public static void getDialogue(int loggedUserId, int userId,
                                   @NonNull final Callback<List<Message>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sMessageService.getDialogue(token.getFormattedToken(), loggedUserId, userId)
                .enqueue(callback);
    }

    public static void writeMessage(@NonNull final Message message,
                                    @NonNull final Callback<Message> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sMessageService.writeMessage(token.getFormattedToken(), message).enqueue(callback);
    }

    public static void findMessagesForUser(int userId, @NonNull final String searchedString,
                                           @NonNull final Callback<List<Message>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sMessageService.findMessagesForUser(token.getFormattedToken(), userId, searchedString)
                .enqueue(callback);
    }

    // FRIEND_REQUESTS_SERVICE
    public static void addRequest(int userId, int requesterId,
                                  @NonNull final Callback<Void> callback) {
        final String body = String.format(Locale.getDefault(),
                FriendsRequestsService.ADD_REQUEST_FORMAT, requesterId, userId);
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);

        sFriendsRequestService.addRequest(token.getFormattedToken(), requestBody).enqueue(callback);
    }

    public static void getRequestsForUser(int userId,
                                          @NonNull final Callback<List<UserBase>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsRequestService.getRequestersForUser(token.getFormattedToken(), userId)
                .enqueue(callback);
    }

    public static void removeRequest(int userId, int requestId,
                                     @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsRequestService.removeRequest(token.getFormattedToken(), userId, requestId)
                .enqueue(callback);
    }

    @SuppressWarnings("unused")
    public static void requestExists(int userId1, int userId2,
                                     @NonNull final Callback<Boolean> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sFriendsRequestService.requestExists(token.getFormattedToken(), userId1, userId2)
                .enqueue(callback);
    }

    // GROUP_SERVICE
    public static void createGroup(@NonNull final Group group,
                                   @NonNull final Callback<Group> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sGroupService.createGroup(token.getFormattedToken(), group)
                .enqueue(callback);
    }

    public static void getGroupById(int groupId, @NonNull final Callback<Group> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sGroupService.getGroupById(token.getFormattedToken(), groupId)
                .enqueue(callback);
    }

    public static void updateGroup(@NonNull final Group group,
                                   @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sGroupService.updateGroup(token.getFormattedToken(), group)
                .enqueue(callback);
    }

    // USER_GROUPS_SERVICE
    public static void addUserToGroup(int userId, int groupId,
                                      @NonNull final Callback<Void> callback) {
        final String body = String.format(Locale.getDefault(),
                UserGroupsService.ADD_USER_TO_GROUP_FORMAT, userId, groupId);
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);

        sUserGroupService.addUserToGroup(token.getFormattedToken(), requestBody).enqueue(callback);
    }

    public static void removeUserFromGroup(int userId, int groupId,
                                           @NonNull final Callback<Void> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserGroupService.removeUserFromGroup(token.getFormattedToken(), userId, groupId)
                .enqueue(callback);
    }

    @SuppressWarnings("unused")
    public static void getGroupsForUser(int userId, @NonNull final Callback<List<Group>> callback) {
        final Token token = MyApplication.getApplication().getSharedPrefHelper().getToken();

        sUserGroupService.getGroupsForUser(token.getFormattedToken(), userId).enqueue(callback);
    }
}
