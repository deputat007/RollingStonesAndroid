package com.rolling_stones.rollingstonesandroid.api;


import com.rolling_stones.rollingstonesandroid.models.Group;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

interface GroupService {

    String BASE_URL = "api/groups/";
    String CREATE_GROUP = BASE_URL + "CreateGroup/";
    String GET_GROUP_BY_ID = BASE_URL + "GetGroupById/";
    String UPDATE_GROUP = BASE_URL + "UpdateGroup/";

    @POST(CREATE_GROUP)
    Call<Group> createGroup(@Header(ApiHeaders.TOKEN_HEADER) String token,
                            @Body Group group);

    @GET(GET_GROUP_BY_ID)
    Call<Group> getGroupById(@Header(ApiHeaders.TOKEN_HEADER) String token,
                             @Query("groupId") int groupId);

    @PUT(UPDATE_GROUP)
    Call<Void> updateGroup(@Header(ApiHeaders.TOKEN_HEADER) String token,
                           @Body Group group);
}
