package com.rolling_stones.rollingstonesandroid.fragments;


import android.support.annotation.StringRes;


public abstract class NavigationTabFragment extends BaseFragment {

    /**
     * The method for changing title {@link android.support.v7.widget.Toolbar#setTitle(int)}
     *
     * @return fragment title;
     */
    @StringRes
    public abstract int getTitle();
}
