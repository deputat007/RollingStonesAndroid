package com.rolling_stones.rollingstonesandroid.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

interface UserDistanceCalculatorService {

    String BASE_URL = "api/usersDistanceCalculator/";
    String CALCULATE_DISTANCE = BASE_URL + "calculateDistance";

    @GET(CALCULATE_DISTANCE)
    Call<Integer> calculateDistance(@Header(ApiHeaders.TOKEN_HEADER) String token,
                                    @Query("userId1") int userId1,
                                    @Query("userId2") int userId2);
}
