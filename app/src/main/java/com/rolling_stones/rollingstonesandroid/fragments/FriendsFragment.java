package com.rolling_stones.rollingstonesandroid.fragments;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.FindUserActivity;
import com.rolling_stones.rollingstonesandroid.activity.SettingsActivity;
import com.rolling_stones.rollingstonesandroid.activity.UserProfileActivity;
import com.rolling_stones.rollingstonesandroid.adapter.UsersAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.Gender;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.OnClickListener;
import com.rolling_stones.rollingstonesandroid.utils.RecyclerTouchListener;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.SEARCH_SERVICE;


public class FriendsFragment extends NavigationTabFragment implements View.OnClickListener,
        OnClickListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, TextWatcher,
        MenuItemCompat.OnActionExpandListener {

    private FloatingActionButton mFABAddFriend;
    private RecyclerViewEmptySupport mRVFriends;
    private SwipeRefreshLayout mRefreshLayout;
    private SearchView mSearchView;

    private UsersAdapter mFriendsAdapter;

    private LinearLayout mContentSearch;
    private AppCompatCheckBox mCheckBoxGender;
    private RadioGroup mRadioGroupGender;
    private AppCompatRadioButton mRadioButtonFemale;
    private AppCompatCheckBox mCheckBoxOnline;

    private AppCompatCheckBox mCheckBoxAge;
    private EditText mEditTextAge;

    public static Fragment getInstance(@Nullable Bundle args) {
        final Fragment fragment = new FriendsFragment();

        if (args != null) {
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_friends;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initUI() {
        mFABAddFriend = findViewById(R.id.fab_find_friend);
        mRVFriends = findViewById(R.id.recycler_view_users);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mContentSearch = getParentActivity().findViewWithId(R.id.content_search);

        mCheckBoxGender = getParentActivity().findViewWithId(R.id.cb_gender);
        mRadioGroupGender = getParentActivity().findViewWithId(R.id.rg_gender);
        mRadioButtonFemale = getParentActivity().findViewWithId(R.id.radio_female);

        mCheckBoxAge = getParentActivity().findViewWithId(R.id.cb_age);
        mEditTextAge = getParentActivity().findViewWithId(R.id.edit_text_age);

        mCheckBoxOnline = getParentActivity().findViewWithId(R.id.cb_online);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setUI(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mFABAddFriend.setOnClickListener(this);

        mFriendsAdapter = new UsersAdapter();

        mRVFriends.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        mRVFriends.addOnItemTouchListener(
                new RecyclerTouchListener(getParentActivity(), this, mRVFriends));
        mRVFriends.setEmptyView(findViewById(R.id.empty_view));
        mRVFriends.setAdapter(mFriendsAdapter);

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);

        updateFriends();

        mCheckBoxGender.setChecked(false);
        mCheckBoxAge.setChecked(false);
        mRadioGroupGender.setVisibility(View.GONE);
        mEditTextAge.setVisibility(View.GONE);
        mRadioGroupGender.setOnCheckedChangeListener(this);

        mCheckBoxGender.setOnCheckedChangeListener(this);
        mCheckBoxAge.setOnCheckedChangeListener(this);
        mCheckBoxOnline.setOnCheckedChangeListener(this);
        mEditTextAge.addTextChangedListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView)
                MenuItemCompat.getActionView(item);
        final SearchManager searchManager = (SearchManager)
                getParentActivity().getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(
                getParentActivity().getComponentName()));

        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final Gender gender = mCheckBoxGender.isChecked() ?
                (mRadioButtonFemale.isChecked() ? Gender.FEMALE : Gender.MALE) : null;
        int age;

        try {
            age = mCheckBoxAge.isChecked() ?
                    Integer.parseInt(mEditTextAge.getText().toString()) : 0;
        } catch (NumberFormatException e) {
            age = 0;
        }

        boolean onlyOnline = mCheckBoxOnline.isChecked();

        ApiHelper.findFriends(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                newText, gender, age, onlyOnline, new MyCallback<List<UserBase>>() {
                    @Override
                    public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mFriendsAdapter.replaceAll(response.body());
                            mRVFriends.scrollToPosition(0);
                        }

                        super.onResponse(call, response);
                    }
                });

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_find_friend:
                startActivity(FindUserActivity.getIntent(getParentActivity(), null));
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {
        final Bundle args = new Bundle();
        args.putInt(Constants.KEY_SELECTED_USER_ID, mFriendsAdapter.getItem(position).getId());

        startActivity(UserProfileActivity.getIntent(getParentActivity(), args));
    }

    @Override
    public void onLongClick(View view, int position) {
    }

    @Override
    public int getTitle() {
        return R.string.title_fragment_friends;
    }

    @Override
    public void onRefresh() {
        updateFriends();
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        mContentSearch.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mContentSearch.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_gender:
                mRadioGroupGender.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (mSearchView != null) {
                    onQueryTextChange(mSearchView.getQuery().toString());
                }
                break;

            case R.id.cb_age:
                mEditTextAge.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (mSearchView != null) {
                    onQueryTextChange(mSearchView.getQuery().toString());
                }
                break;

            case R.id.cb_online:
                if (mSearchView != null) {
                    onQueryTextChange(mSearchView.getQuery().toString());
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (mSearchView != null) {
            onQueryTextChange(mSearchView.getQuery().toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mSearchView != null) {
            onQueryTextChange(mSearchView.getQuery().toString());
        }
    }

    /**
     * The method calls when logged user add some friends
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(FriendsUpdateEvent event) {
        updateFriends();
    }

    /**
     * The method calls when list order changed;
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(SettingsUpdateEvent event) {
        mFriendsAdapter.clear();
        updateFriends();
    }

    /**
     * The method updates friends {@link #mFriendsAdapter}.
     */
    private void updateFriends() {
        mRefreshLayout.setRefreshing(true);
        ApiHelper.getFriends(MyApplication.getApplication().getLoggedUserId(),
                new MyCallback<List<UserBase>>() {
                    @Override
                    public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mFriendsAdapter.replaceAll(response.body());
                        }

                        mRefreshLayout.setRefreshing(false);
                        super.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<List<UserBase>> call, Throwable t) {
                        mRefreshLayout.setRefreshing(false);
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * Event for updating friend list {@link #mRVFriends} when user add some friends
     */
    public static class FriendsUpdateEvent {
    }

    /**
     * Event settings (list order)
     * {@link com.rolling_stones.rollingstonesandroid.activity.SettingsActivity#onFontSizeEvent(SettingsActivity.FontSizeEvent)};
     */
    public static class SettingsUpdateEvent {
    }
}
