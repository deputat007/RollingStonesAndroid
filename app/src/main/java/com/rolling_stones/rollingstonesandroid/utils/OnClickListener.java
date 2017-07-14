package com.rolling_stones.rollingstonesandroid.utils;

import android.view.View;


public interface OnClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
