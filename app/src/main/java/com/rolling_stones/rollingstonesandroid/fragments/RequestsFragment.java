package com.rolling_stones.rollingstonesandroid.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.adapter.RequestAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class RequestsFragment extends NavigationTabFragment implements
        SwipeRefreshLayout.OnRefreshListener, RequestAdapter.OnClickListener {

    private RecyclerViewEmptySupport mRecyclerViewRequests;
    private SwipeRefreshLayout mRefreshLayout;

    private RequestAdapter mAdapter;

    public static Fragment getInstance(@Nullable Bundle args) {
        final Fragment fragment = new RequestsFragment();

        if (args != null) {
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_requests;
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
        mRecyclerViewRequests = findViewById(R.id.recycler_view_users);
        mRefreshLayout = findViewById(R.id.refresh_layout);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setUI(@Nullable Bundle savedInstanceState) {
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter = new RequestAdapter(this);
        mRecyclerViewRequests.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        mRecyclerViewRequests.setEmptyView(findViewById(R.id.empty_view));
        mRecyclerViewRequests.setAdapter(mAdapter);

        onRefresh();
    }

    @Override
    public int getTitle() {
        return R.string.title_fragment_requests;
    }

    @Override
    public void onRefresh() {
        updateRequests();
    }

    @Override
    public void onAcceptClicked(final int position, @NonNull View v) {
        ApiHelper.addFriends(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                mAdapter.getItem(position).getId(), new MyCallback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            mAdapter.remove(mAdapter.getItem(position));
                            EventBus.getDefault().post(new FriendsFragment.FriendsUpdateEvent());
                        }
                    }
                });
    }

    @Override
    public void onRemoveClicked(final int position, @NonNull View v) {
        ApiHelper.removeRequest(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                mAdapter.getItem(position).getId(), new MyCallback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            mAdapter.remove(mAdapter.getItem(position));
                        }
                    }
                });
    }

    /**
     * The method calls when list order changed;
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(FriendsFragment.SettingsUpdateEvent event) {
        mAdapter.clear();
        updateRequests();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(SignalRRequestEvent event) {
        mAdapter.add(event.mUserBase);
    }

    /**
     * The method updates requests {@link #mAdapter}.
     */
    private void updateRequests() {
        mRefreshLayout.setRefreshing(true);

        ApiHelper.getRequestsForUser(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                new MyCallback<List<UserBase>>() {
                    @Override
                    public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mAdapter.replaceAll(response.body());
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
     * Event for SignalR
     */
    public static class SignalRRequestEvent {
        private final UserBase mUserBase;

        public SignalRRequestEvent(UserBase userBase) {
            mUserBase = userBase;
        }
    }
}
