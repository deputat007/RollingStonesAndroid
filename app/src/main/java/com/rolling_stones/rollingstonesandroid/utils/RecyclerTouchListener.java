package com.rolling_stones.rollingstonesandroid.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private OnClickListener mOnClickListener;
    private GestureDetector mGestureDetector;

    public RecyclerTouchListener(@NonNull final Context context,
                                 @NonNull final OnClickListener onClickListener,
                                 @NonNull final RecyclerView recyclerView) {
        mOnClickListener = onClickListener;

        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        final View view = recyclerView.findChildViewUnder(e.getX(), e.getY());

                        if (view != null && mOnClickListener != null) {
                            mOnClickListener.onLongClick(view,
                                    recyclerView.getChildAdapterPosition(view));
                        }
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        final View childView = rv.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && mOnClickListener != null && mGestureDetector.onTouchEvent(e)) {
            mOnClickListener.onClick(childView, rv.getChildAdapterPosition(childView));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
