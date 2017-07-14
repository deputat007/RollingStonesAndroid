package com.rolling_stones.rollingstonesandroid.api;


import com.rolling_stones.rollingstonesandroid.models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


interface MessageService {

    String BASE_URL = "api/messages/";
    String GET_LAST_MESSAGES = BASE_URL + "getLastMessages";
    String GET_LAST_MESSAGES_FOR_GROUP = BASE_URL + "getMessagesForGroup";
    String GET_DIALOGUE = BASE_URL + "getDialogue";
    String WRITE_MESSAGE = BASE_URL + "writeMessage";
    String FIND_MESSAGES_FOR_USER = BASE_URL + "findMessagesForUser";

    @GET(GET_LAST_MESSAGES)
    Call<List<Message>> getLastMessages(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                        @Query("userId") int userId);

    @GET(GET_LAST_MESSAGES_FOR_GROUP)
    Call<List<Message>> getLastMessagesForGroup(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                                @Query("groupId") int groupId);

    @GET(GET_DIALOGUE)
    Call<List<Message>> getDialogue(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                    @Query("userId1") int userId1,
                                    @Query("userId2") int userId2);

    @POST(WRITE_MESSAGE)
    Call<Message> writeMessage(@Header(ApiHeaders.TOKEN_HEADER) String token,
                               @Body Message message);

    @GET(FIND_MESSAGES_FOR_USER)
    Call<List<Message>> findMessagesForUser(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                            @Query("userId") int userId,
                                            @Query("searchedString") String searchedString);
}
