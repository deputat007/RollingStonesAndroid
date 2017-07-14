package com.rolling_stones.rollingstonesandroid.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rolling_stones.rollingstonesandroid.R;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mTopSpace;
    private final int mLeftSpace;
    private final int mRightSpace;
    private final int mBottomSpace;

    private SpaceItemDecoration(int topSpace, int leftSpace, int rightSpace, int bottomSpace) {
        mTopSpace = topSpace;
        mLeftSpace = leftSpace;
        mRightSpace = rightSpace;
        mBottomSpace = bottomSpace;
    }

    @SuppressWarnings("unused")
    public SpaceItemDecoration(int space) {
        mTopSpace = space;
        mLeftSpace = space;
        mRightSpace = space;
        mBottomSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.top = mTopSpace;
        outRect.left = mLeftSpace;
        outRect.bottom = mBottomSpace;
        outRect.right = mRightSpace;
    }

    public static RecyclerView.ItemDecoration getDefaultSpaceItemDecoration(
            @NonNull final Context context) {
        return new SpaceItemDecoration((int) context.getResources().getDimension(R.dimen.offset_tiny),
                (int) context.getResources().getDimension(R.dimen.offset_medium),
                (int) context.getResources().getDimension(R.dimen.offset_medium),
                (int) context.getResources().getDimension(R.dimen.offset_tiny));
    }
}
