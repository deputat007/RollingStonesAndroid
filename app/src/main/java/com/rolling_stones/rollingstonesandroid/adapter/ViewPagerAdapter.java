package com.rolling_stones.rollingstonesandroid.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.rolling_stones.rollingstonesandroid.fragments.FriendsFragment;
import com.rolling_stones.rollingstonesandroid.fragments.MessagesFragment;
import com.rolling_stones.rollingstonesandroid.fragments.ProfileFragment;
import com.rolling_stones.rollingstonesandroid.fragments.RequestsFragment;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return ProfileFragment.getInstance(null);
            case 1:
                return FriendsFragment.getInstance(null);
            case 2:
                return MessagesFragment.getInstance(null);
            case 3:
                return RequestsFragment.getInstance(null);
        }
    }
}
