package com.rolling_stones.rollingstonesandroid.api;


public interface StatusCode {

    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int CONFLICT = 409;

    int INTERNAL_SERVER_ERROR = 500;
}
