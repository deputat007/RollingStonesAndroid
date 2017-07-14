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


interface FriendsRequestsService {

    String BASE_URL = "api/friendRequests/";
    String ADD_REQUEST = BASE_URL + "addRequest/";
    String GET_REQUESTERS_FOR_USER = BASE_URL + "getRequestersForUser/";
    String REMOVE_REQUEST = BASE_URL + "removeRequest/";
    String REQUEST_EXISTS = BASE_URL + "requestExists/";

    String ADD_REQUEST_FORMAT = "userId=%d&requesterId=%d";

    @Headers(ApiHeaders.CONTENT_TYPE_HEADER)
    @POST(ADD_REQUEST)
    Call<Void> addRequest(@Header(ApiHeaders.TOKEN_HEADER) String token, @Body RequestBody body);

    @GET(GET_REQUESTERS_FOR_USER)
    Call<List<UserBase>> getRequestersForUser(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                              @Query("userId") int userId);

    @DELETE(REMOVE_REQUEST)
    Call<Void> removeRequest(@Header(ApiHeaders.TOKEN_HEADER) String token,
                             @Query("userId") int userId,
                             @Query("requesterId") int requestId);

    @GET(REQUEST_EXISTS)
    Call<Boolean> requestExists(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                @Query("userId1") int userId1,
                                @Query("userId2") int userId2);
}
