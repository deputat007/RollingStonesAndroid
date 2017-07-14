package com.rolling_stones.rollingstonesandroid.api;


import com.rolling_stones.rollingstonesandroid.models.Token;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

interface AuthorizationService {
    String BASE_URL = "token";

    String USER_TOKEN_FORMAT = "username=%s&password=%s&grant_type=password";

    @Headers(ApiHeaders.CONTENT_TYPE_HEADER)
    @POST(BASE_URL)
    Call<Token> getToken(@Body RequestBody body);
}
