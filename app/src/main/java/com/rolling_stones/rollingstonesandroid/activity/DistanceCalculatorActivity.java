package com.rolling_stones.rollingstonesandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.adapter.SelectUsersAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.OnClickListener;
import com.rolling_stones.rollingstonesandroid.utils.RecyclerTouchListener;
import com.rolling_stones.rollingstonesandroid.utils.SpaceItemDecoration;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class DistanceCalculatorActivity extends BaseActivity {

    private RecyclerViewEmptySupport mLeftUsers;
    private RecyclerViewEmptySupport mRightUsers;

    private SelectUsersAdapter mLeftAdapter;
    private SelectUsersAdapter mRightAdapter;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, DistanceCalculatorActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_distance_calculator;
    }

    @Override
    protected void initUI() {
        mLeftUsers = findViewWithId(R.id.left_users);
        mRightUsers = findViewWithId(R.id.right_users);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        setTitle(R.string.title_distance_calculator);
        setDisplayHomeAsUpEnabled(true);
        ApiHelper.findUsers("", null, 0, false, new MyCallback<List<UserBase>>() {
            @Override
            public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mLeftAdapter = new SelectUsersAdapter(response.body());
                    mLeftUsers.setAdapter(mLeftAdapter);

                    mRightAdapter = new SelectUsersAdapter(response.body());
                    mRightUsers.setAdapter(mRightAdapter);
                }

                super.onResponse(call, response);
            }
        });

        mLeftUsers.setLayoutManager(new LinearLayoutManager(this));
        mLeftUsers.addOnItemTouchListener(new RecyclerTouchListener(this, new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                mLeftAdapter.singleSelection(position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }, mLeftUsers));

        mLeftUsers.addItemDecoration(SpaceItemDecoration.getDefaultSpaceItemDecoration(this));

        mRightUsers.setLayoutManager(new LinearLayoutManager(this));
        mRightUsers.addOnItemTouchListener(new RecyclerTouchListener(this, new OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                mRightAdapter.singleSelection(position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }, mRightUsers));

        mRightUsers.addItemDecoration(SpaceItemDecoration.getDefaultSpaceItemDecoration(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accept, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_accept:
                if (mLeftAdapter.getSelectedItemCount() == 0 ||
                        mRightAdapter.getSelectedItemCount() == 0) {
                    Toast.makeText(DistanceCalculatorActivity.this, R.string.text_select_users,
                            Toast.LENGTH_SHORT).show();
                } else {
                    ApiHelper.calculateDistance(mLeftAdapter.getSelectedIds()[0],
                            mRightAdapter.getSelectedIds()[0], new MyCallback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(DistanceCalculatorActivity.this,
                                                getString(R.string.text_distance) + response.body(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    super.onResponse(call, response);
                                }
                            });
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
