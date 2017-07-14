package com.rolling_stones.rollingstonesandroid.api;


import com.rolling_stones.rollingstonesandroid.models.Group;

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

interface UserGroupsService {

    String BASE_URL = "api/userGroups/";
    String ADD_USER_TO_GROUP = BASE_URL + "AddUserToGroup/";
    String REMOVE_USER_FROM_GROUP = BASE_URL + "RemoveUserFromGroup/";
    String GET_GROUPS_FOR_USER = BASE_URL + "GetGroupsForUser/";

    String ADD_USER_TO_GROUP_FORMAT = "userId=%d&groupId=%d";

    @Headers(ApiHeaders.CONTENT_TYPE_HEADER)
    @POST(ADD_USER_TO_GROUP)
    Call<Void> addUserToGroup(@Header(ApiHeaders.TOKEN_HEADER) String token,
                              @Body RequestBody body);

    @DELETE(REMOVE_USER_FROM_GROUP)
    Call<Void> removeUserFromGroup(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                   @Query("userId") int userId,
                                   @Query("groupId") int groupId);

    @GET(GET_GROUPS_FOR_USER)
    Call<List<Group>> getGroupsForUser(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                       @Query("userId") int userId);
}
