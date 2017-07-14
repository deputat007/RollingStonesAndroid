package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.adapter.ViewPagerAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.fragments.NavigationTabFragment;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.services.SignalRService;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.LogoutDialogFragment;

import java.util.ArrayList;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;
import retrofit2.Call;
import retrofit2.Response;

import static com.rolling_stones.rollingstonesandroid.application.Constants.KEY_CURRENT_POSITION;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private NavigationTabBar mNavigationTabBar;
    private ViewPager mViewPager;
    private LinearLayout mContentSearch;

    private int mCurrentPosition;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, MainActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initUI() {
        mNavigationTabBar = findViewWithId(R.id.ntb);
        mViewPager = findViewWithId(R.id.view_pager);
        mContentSearch = findViewWithId(R.id.content_search);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setUI(@Nullable Bundle savedInstanceState) {
        getApplicationContext().startService(new Intent(this, SignalRService.class));
        setTitle(R.string.title_activity_main);
        checkIsUserDeleted();

        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(viewPagerAdapter);
        mContentSearch.setVisibility(View.GONE);

        final List<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_account),
                        getResources().getColor(R.color.color_current_ntb_item))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_account_multiple),
                        getResources().getColor(R.color.color_current_ntb_item))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_wechat),
                        getResources().getColor(R.color.color_current_ntb_item))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_account_multiple_plus),
                        getResources().getColor(R.color.color_current_ntb_item))
                        .build()
        );

        mNavigationTabBar.setModels(models);
        mCurrentPosition = savedInstanceState != null ?
                savedInstanceState.getInt(KEY_CURRENT_POSITION, 0) : 0;
        setFragmentTitle();
        mNavigationTabBar.setViewPager(mViewPager, mCurrentPosition);
        mNavigationTabBar.setOnPageChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().stopService(new Intent(this, SignalRService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment :
                getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivity(SettingsActivity.getIntent(this, null));
                return true;

            case R.id.item_distance_calculator:
                startActivity(DistanceCalculatorActivity.getIntent(this, null));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_CURRENT_POSITION, mCurrentPosition);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        if (position != 1) {
            mContentSearch.setVisibility(View.GONE);
        }
        setFragmentTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * The method updates title {@link android.support.v7.widget.Toolbar#setTitle(int)}
     */
    private void setFragmentTitle() {
        final NavigationTabFragment currentFragment = ((NavigationTabFragment)
                ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(mCurrentPosition));

        setTitle(currentFragment.getTitle());
    }

    private void checkIsUserDeleted() {
        ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(), new MyCallback<UserBase>() {
            @Override
            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isDeleted()) {
                        startActivity(AccountDeletedActivity.getIntent(MainActivity.this, null));
                        finish();
                    }
                }

                super.onResponse(call, response);
            }
        });
    }

    /**
     * Event for {@link LogoutDialogFragment}
     */
    public static class LogoutEvent {
    }
}
