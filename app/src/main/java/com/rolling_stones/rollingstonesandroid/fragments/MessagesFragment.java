package com.rolling_stones.rollingstonesandroid.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.ChatActivity;
import com.rolling_stones.rollingstonesandroid.activity.ChatGroupActivity;
import com.rolling_stones.rollingstonesandroid.adapter.DialoguesAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.api.StatusCode;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.Group;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.OnClickListener;
import com.rolling_stones.rollingstonesandroid.utils.RecyclerTouchListener;
import com.rolling_stones.rollingstonesandroid.utils.SoundNotificationHelper;
import com.rolling_stones.rollingstonesandroid.utils.dialogs.SelectFriendDialogFragment;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.SEARCH_SERVICE;


public class MessagesFragment extends NavigationTabFragment implements View.OnClickListener,
        OnClickListener, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private RecyclerViewEmptySupport mRecyclerViewDialogues;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mFABAddDialogue;
    private FloatingActionButton mFABAddGroup;
    private SwipeRefreshLayout mRefreshLayout;

    private DialoguesAdapter mDialoguesAdapter;

    public static Fragment getInstance(@Nullable Bundle args) {
        final Fragment fragment = new MessagesFragment();

        if (args != null) {
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_messages;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initUI() {
        mFloatingActionMenu = findViewById(R.id.fab_menu);
        mFABAddDialogue = findViewById(R.id.fab_menu_item_add_dialogue);
        mFABAddGroup = findViewById(R.id.fab_menu_item_add_group);
        mRecyclerViewDialogues = findViewById(R.id.recycler_view_dialogues);
        mRefreshLayout = findViewById(R.id.refresh_layout);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void setUI(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
        mFloatingActionMenu.setClosedOnTouchOutside(true);
        mFABAddDialogue.setOnClickListener(this);
        mFABAddGroup.setOnClickListener(this);

        mDialoguesAdapter = new DialoguesAdapter(DialoguesAdapter.DEFAULT_COMPARATOR);

        mRecyclerViewDialogues.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        mRecyclerViewDialogues.addItemDecoration(new DividerItemDecoration(getParentActivity(),
                DividerItemDecoration.VERTICAL));
        mRecyclerViewDialogues.addOnItemTouchListener(
                new RecyclerTouchListener(getParentActivity(), this, mRecyclerViewDialogues));
        mRecyclerViewDialogues.setEmptyView(findViewById(R.id.empty_view));
        mRecyclerViewDialogues.setAdapter(mDialoguesAdapter);

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);

        updateDialogues();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFloatingActionMenu.close(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        final SearchManager searchManager = (SearchManager)
                getParentActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                getParentActivity().getComponentName()));

        searchView.setOnQueryTextListener(this);
    }

    @Override
    public int getTitle() {
        return R.string.title_fragment_messages;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_menu_item_add_dialogue:
                final DialogFragment dialogFragment1 = SelectFriendDialogFragment.newInstance(
                        SelectFriendDialogFragment.SINGLE_SELECTION_MODE);
                dialogFragment1.show(getFragmentManager(), Constants.TAG_DIALOG_SELECT_FRIENDS);
                break;

            case R.id.fab_menu_item_add_group:
                final DialogFragment dialogFragment2 = SelectFriendDialogFragment.newInstance(
                        SelectFriendDialogFragment.MULTI_SELECTION_MODE);
                dialogFragment2.show(getFragmentManager(), Constants.TAG_DIALOG_SELECT_FRIENDS);
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {
        final Bundle args = new Bundle();

        final Message message = mDialoguesAdapter.getItem(position);
        int loggedUserId = MyApplication.getApplication().getLoggedUserId();

        if (message.getGroup() != null) {
            args.putInt(Constants.KEY_SELECTED_GROUP_ID, message.getGroup().getId());
            startActivity(ChatGroupActivity.getIntent(getParentActivity(), args));
            return;
        }

        if (message.getSender().getId() == loggedUserId) {
            args.putInt(Constants.KEY_SELECTED_USER_ID, message.getRecipient().getId());
        } else {
            args.putInt(Constants.KEY_SELECTED_USER_ID, message.getSender().getId());
        }

        startActivity(ChatActivity.getIntent(getParentActivity(), args));
    }

    @Override
    public void onLongClick(View view, int position) {
    }

    @Override
    public void onRefresh() {
        updateDialogues();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            ApiHelper.findMessagesForUser(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                    newText, new MyCallback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            if (response.isSuccessful()) {
                                mDialoguesAdapter.replaceAll(response.body());
                                mRecyclerViewDialogues.scrollToPosition(0);
                            }

                            super.onResponse(call, response);
                        }

                        @Override
                        protected int handleError(int statusCode) {
                            switch (statusCode) {
                                case StatusCode.NOT_FOUND:
                                    return R.string.error_message_not_found;

                                default:
                                    return super.handleError(statusCode);
                            }
                        }
                    });
        }

        return true;
    }

    /**
     * This method is calling when user create new chat(1 to 1 {@link ChatActivity}
     * or group chat {@link ChatGroupActivity}) {@link SelectFriendDialogFragment}
     *
     * @param event;
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(SelectFriendEvent event) {
        final int[] ids = event.ids;

        if (ids != null) {
            if (ids.length == 1) {
                final Bundle args = new Bundle();
                args.putInt(Constants.KEY_SELECTED_USER_ID, ids[0]);
                startActivity(ChatActivity.getIntent(getParentActivity(), args));
            } else {
                final String groupName = event.groupTitle;
                createGroupChat(ids, groupName);
            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(final SignalREvent event) {
        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addOrUpdate(event.mMessage);
            }
        });
    }

    /**
     * The method updates dialogues {@link #mDialoguesAdapter}.
     */
    private void updateDialogues() {
        mRefreshLayout.setRefreshing(true);
        ApiHelper.getLastMessages(getParentActivity().getSharedPrefHelper().getLoggedUserId(),
                new MyCallback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mDialoguesAdapter.replaceAll(response.body());
                        }

                        mRefreshLayout.setRefreshing(false);
                        super.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {
                        mRefreshLayout.setRefreshing(false);
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * The method creates new group chat {@link ChatGroupActivity}
     *
     * @param ids;
     * @param groupName;
     */
    private void createGroupChat(final int[] ids, final String groupName) {
        final List<UserBase> members = new ArrayList<>();

        for (int i :
                ids) {
            members.add(new UserBase(i));
        }
        final Group group = new Group(0, groupName, members);

        ApiHelper.createGroup(group, new MyCallback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    int groupId = response.body().getId();
                    final Bundle args = new Bundle();

                    args.putInt(Constants.KEY_SELECTED_GROUP_ID, groupId);
                    startActivity(ChatGroupActivity.getIntent(getParentActivity(), args));
                }

                super.onResponse(call, response);
            }
        });

    }

    /**
     * The method updates or creates new dialogue and plays sound notification
     * {@link SoundNotificationHelper#playSound(Context)};
     *
     * @param message;
     */
    private void addOrUpdate(final Message message) {
//        if (message.getGroup() == null) {
//            for (int i = 0; i < mDialoguesAdapter.getItemCount(); i++) {
//                final Message model = mDialoguesAdapter.getItem(i);
//
//                if (model.getGroup() == null) {
//                    if ((model.getRecipient().getId() == message.getRecipient().getId() &&
//                            model.getSender().getId() == message.getSender().getId()) ||
//                            (model.getSender().getId() == message.getRecipient().getId() &&
//                                    model.getRecipient().getId() == message.getSender().getId())) {
//                        mDialoguesAdapter.remove(model);
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < mDialoguesAdapter.getItemCount(); i++) {
//                final Message model = mDialoguesAdapter.getItem(i);
//
//                if (model.getGroup() != null &&
//                        model.getGroup().getId() == message.getGroup().getId()) {
//                    mDialoguesAdapter.remove(model);
//                }
//            }
//        }
//
//        mDialoguesAdapter.add(message);
        updateDialogues();
    }

    /**
     * Event for updating dialogues
     */
    public static class SignalREvent {
        final Message mMessage;

        public SignalREvent(Message message) {
            mMessage = message;
        }

        public Message getMessage() {
            return mMessage;
        }
    }

    /**
     * Event for {@link SelectFriendDialogFragment}
     */
    public static class SelectFriendEvent {
        private final int[] ids;
        private final String groupTitle;

        public SelectFriendEvent(int[] ids, String groupTitle) {
            this.ids = ids;
            this.groupTitle = groupTitle;
        }
    }
}
