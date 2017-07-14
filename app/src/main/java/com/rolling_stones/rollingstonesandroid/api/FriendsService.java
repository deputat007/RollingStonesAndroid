package com.rolling_stones.rollingstonesandroid.api;


import com.rolling_stones.rollingstonesandroid.models.UserBase;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


interface FriendsService {

    String BASE_URL = "api/friends/";
    String ADD_FRIENDS = BASE_URL + "addFriends/";
    String REMOVE_FRIENDS = BASE_URL + "removeFriends/";
    String ARE_FRIENDS = BASE_URL + "areFriends/";
    String GET_FRIENDS = BASE_URL + "getFriends/";
    String FIND_FRIENDS = BASE_URL + "findFriends/";

    String ADD_FRIENDS_FORMAT = "userId=%d&requesterId=%d";

    @Headers(ApiHeaders.CONTENT_TYPE_HEADER)
    @POST(ADD_FRIENDS)
    Call<Void> addFriends(@Header(ApiHeaders.TOKEN_HEADER) String token,
                          @Body RequestBody body);

    @DELETE(REMOVE_FRIENDS)
    Call<Void> removeFriends(@Header(ApiHeaders.TOKEN_HEADER) String token,
                             @Query("userId") int userId,
                             @Query("friendId") int friendId);

    @GET(ARE_FRIENDS)
    Call<Boolean> areFriends(@Header(ApiHeaders.TOKEN_HEADER) String token,
                             @Query("userId") int userId,
                             @Query("friendId") int friendId);

    @GET(GET_FRIENDS)
    Call<List<UserBase>> getFriends(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                    @Query("userId") int userId);

    @GET(FIND_FRIENDS)
    Call<List<UserBase>> findFriends(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                     @Query("userId") int userId,
                                     @Query("searchedString") String searchedString,
                                     @Query("gender") String gender,
                                     @Query("age") Integer age,
                                     @Query("onlyOnline") boolean onlyOnline);
}
