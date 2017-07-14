package com.rolling_stones.rollingstonesandroid.activity;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.adapter.UsersAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.models.Gender;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.OnClickListener;
import com.rolling_stones.rollingstonesandroid.utils.RecyclerTouchListener;
import com.rolling_stones.rollingstonesandroid.utils.SpaceItemDecoration;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class FindUserActivity extends BaseActivity implements SearchView.OnQueryTextListener,
        OnClickListener, MenuItemCompat.OnActionExpandListener, CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener, TextWatcher {

    private UsersAdapter mUsersAdapter;
    private RecyclerViewEmptySupport mRVUsers;
    private SearchView mSearchView;

    private LinearLayout mContentSearch;
    private AppCompatCheckBox mCheckBoxGender;
    private RadioGroup mRadioGroupGender;
    private AppCompatRadioButton mRadioButtonFemale;

    private AppCompatCheckBox mCheckBoxAge;
    private EditText mEditTextAge;
    private AppCompatCheckBox mCheckBoxOnline;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, FindUserActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_find_user;
    }

    @Override
    protected void initUI() {
        mRVUsers = findViewWithId(R.id.recycler_view_users);
        mContentSearch = findViewWithId(R.id.content_search);

        mCheckBoxGender = findViewWithId(R.id.cb_gender);
        mRadioGroupGender = findViewWithId(R.id.rg_gender);
        mRadioButtonFemale = findViewWithId(R.id.radio_female);

        mCheckBoxAge = findViewWithId(R.id.cb_age);
        mEditTextAge = findViewWithId(R.id.edit_text_age);

        mCheckBoxOnline = findViewWithId(R.id.cb_online);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_activity_find_user);
        mContentSearch.setVisibility(View.GONE);

        mUsersAdapter = new UsersAdapter();

        updateList();

        mRVUsers.setLayoutManager(new LinearLayoutManager(this));
        mRVUsers.addItemDecoration(SpaceItemDecoration.getDefaultSpaceItemDecoration(this));
        mRVUsers.addOnItemTouchListener(
                new RecyclerTouchListener(this, this, mRVUsers));
        mRVUsers.setEmptyView(findViewById(R.id.empty_view));
        mRVUsers.setAdapter(mUsersAdapter);

        mCheckBoxGender.setChecked(false);
        mCheckBoxAge.setChecked(false);
        mRadioGroupGender.setVisibility(View.GONE);
        mEditTextAge.setVisibility(View.GONE);
        mRadioGroupGender.setOnCheckedChangeListener(this);

        mCheckBoxGender.setOnCheckedChangeListener(this);
        mCheckBoxAge.setOnCheckedChangeListener(this);
        mEditTextAge.addTextChangedListener(this);
        mCheckBoxOnline.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView)
                MenuItemCompat.getActionView(item);
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
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

        ApiHelper.findUsers(newText, gender, age, onlyOnline, new MyCallback<List<UserBase>>() {
            @Override
            public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mUsersAdapter.replaceAll(response.body());
                    mRVUsers.scrollToPosition(0);
                }

                super.onResponse(call, response);
            }
        });
        return true;
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
    public void onClick(View view, int position) {
        final Bundle args = new Bundle();
        args.putInt(Constants.KEY_SELECTED_USER_ID, mUsersAdapter.getItem(position).getId());

        startActivity(UserProfileActivity.getIntent(FindUserActivity.this, args));
    }

    @Override
    public void onLongClick(View view, int position) {
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
     * The method updates users {@link #mUsersAdapter}.
     */
    private void updateList() {
        ApiHelper.findUsers("", null, 0, false, new MyCallback<List<UserBase>>() {
            @Override
            public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mUsersAdapter.replaceAll(response.body());
                    mRVUsers.scrollToPosition(0);
                }

                super.onResponse(call, response);
            }
        });
    }
}
