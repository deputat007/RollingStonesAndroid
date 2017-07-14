package com.rolling_stones.rollingstonesandroid.api;


import com.rolling_stones.rollingstonesandroid.models.User;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.models.UserBaseWithState;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;


interface UserService {
    String BASE_URL = "api/users/";
    String GET_USER_BY_ID = BASE_URL + "getUserById";
    String CREATE_USER = BASE_URL + "createUser";
    String UPDATE_USER = BASE_URL + "updateUser";
    String UPDATE_USER_PASSWORD = BASE_URL + "updateUserPassword";
    String FIND_USERS = BASE_URL + "findUsers";
    String DELETE_USER = BASE_URL + "deleteUser";
    String RECOVER_USER = BASE_URL + "recoverUser";
    String GET_USER_WITH_STATE = BASE_URL + "getUserWithState";

    String UPDATE_USER_PASSWORD_FORMAT = "userId=%d&oldPassword=%s&newPassword=%s";
    String DELETE_USER_FORMAT = "userId=%d";

    @POST(CREATE_USER)
    Call<Void> createUser(@Body User user);

    @GET(GET_USER_BY_ID)
    Call<UserBase> getUserById(@Header(ApiHeaders.TOKEN_HEADER) String token,
                               @Query("userId") int userId);

    @PUT(UPDATE_USER)
    Call<Void> updateUser(@Header(ApiHeaders.TOKEN_HEADER) String token,
                          @Body UserBase user);

    @GET(FIND_USERS)
    Call<List<UserBase>> findUsers(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                   @Query("searchedString") String searchedString,
                                   @Query("gender") String gender,
                                   @Query("age") Integer age,
                                   @Query("onlyOnline") boolean onlyOnline);

    @Headers(ApiHeaders.CONTENT_TYPE_HEADER)
    @PUT(UPDATE_USER_PASSWORD)
    Call<Void> updateUserPassword(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                  @Body RequestBody body);

    @Headers(ApiHeaders.CONTENT_TYPE_HEADER)
    @POST(DELETE_USER)
    Call<Void> deleteUser(@Header(ApiHeaders.TOKEN_HEADER) String token,
                          @Body RequestBody body);

    @DELETE(RECOVER_USER)
    Call<Void> recoverUser(@Header(ApiHeaders.TOKEN_HEADER) String token,
                           @Query("userId") int userId);

    @GET(GET_USER_WITH_STATE)
    Call<UserBaseWithState> getUserWithState(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                             @Query("userId") int userId,
                                             @Query("currentUserId") int currentUserId);
}
