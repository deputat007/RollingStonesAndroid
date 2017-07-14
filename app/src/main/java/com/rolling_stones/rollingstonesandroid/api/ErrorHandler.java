package com.rolling_stones.rollingstonesandroid.api;


import android.support.annotation.StringRes;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;


public class ErrorHandler {

    @StringRes
    public static int handleError(int statusCode) {
        switch (statusCode) {
            case StatusCode.BAD_REQUEST:
                return R.string.error_bad_request;

            case StatusCode.UNAUTHORIZED:
                MyApplication.getApplication().logout();
                return R.string.error_unauthorized;

            case StatusCode.FORBIDDEN:
                return R.string.error_forbidden;

            case StatusCode.NOT_FOUND:
                return R.string.error_not_found;

            case StatusCode.CONFLICT:
                return R.string.error_conflict;

            case StatusCode.INTERNAL_SERVER_ERROR:
                return R.string.error_internal_server;
        }

        return R.string.error_unknown;
    }
}
