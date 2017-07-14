package com.rolling_stones.rollingstonesandroid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.adapter.MessageAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.Constants;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.GlideLoader;
import com.rolling_stones.rollingstonesandroid.utils.SoundNotificationHelper;
import com.rolling_stones.rollingstonesandroid.utils.SpaceItemDecoration;
import com.rolling_stones.rollingstonesandroid.utils.VibratorServiceHelper;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 1-to-1 chat
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener, MessageAdapter.OnUserIconClickListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        EmojiconGridFragment.OnEmojiconClickedListener {

    private RecyclerViewEmptySupport mRecyclerViewChat;
    private ImageView mImageViewSend;
    private EmojiconEditText mEditTextMessage;
    private ImageView mImageViewSwitcher;
    private FrameLayout mEmojicons;

    private MessageAdapter mMessageAdapter;
    private List<Message> mMessages;
    private int mRecipientId;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, ChatActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initUI() {
        mRecyclerViewChat = findViewWithId(R.id.recycler_view_chat);
        mImageViewSend = findViewWithId(R.id.image_view_send);
        mEditTextMessage = findViewWithId(R.id.edit_text_message);
        mImageViewSwitcher = findViewWithId(R.id.image_view_switcher);
        mEmojicons = findViewWithId(R.id.emojicons);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        mRecipientId = getIntent().getExtras().getInt(Constants.KEY_SELECTED_USER_ID);

        setRecipientTitle();
        setDisplayHomeAsUpEnabled(true);
        mImageViewSend.setOnClickListener(this);
        mImageViewSwitcher.setOnClickListener(this);

        mMessageAdapter = new MessageAdapter(MessageAdapter.DEFAULT_COMPARATOR,
                MyApplication.getApplication().getSharedPrefHelper().getLoggedUserId(), this);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerViewChat.addItemDecoration(SpaceItemDecoration.getDefaultSpaceItemDecoration(this));
        manager.setStackFromEnd(true);

        mRecyclerViewChat.setLayoutManager(manager);
        mRecyclerViewChat.setEmptyView(findViewById(R.id.empty_view));
        mRecyclerViewChat.setAdapter(mMessageAdapter);

        mEditTextMessage.addTextChangedListener(new TextValidator(mEditTextMessage));
        mEditTextMessage.setOnClickListener(this);
        validateMessage();

        updateMessages();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, new EmojiconsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        mMessageAdapter.filter(mMessages, newText);
        scrollMessagesToBottom();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_send:
                if (validateMessage()) {
                    writeMessage();
                } else {
                    promptSpeechInput();
                }
                break;

            case R.id.image_view_switcher:
                if (mEmojicons.getVisibility() != View.VISIBLE) {
                    showEmojicons();
                } else {
                    hideEmojicons();
                }
                break;

            case R.id.edit_text_message:
                hideEmojicons();
                break;
        }
    }

    @Override
    public void onClick(@NonNull View view, int position) {
        if (mMessageAdapter.getItem(position).getGroup() == null) {
            final Bundle args = new Bundle();
            args.putInt(Constants.KEY_SELECTED_USER_ID,
                    mMessageAdapter.getItem(position).getSender().getId());

            startActivity(UserProfileActivity.getIntent(ChatActivity.this, args));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    final ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mEditTextMessage.setText(result.get(0));
                }

                break;
            }

        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        final String text = mEditTextMessage.getText().toString();
        if (text.length() > 0) {
            mEditTextMessage.setText(text.substring(0, text.length() - 1));
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        final String text = mEditTextMessage.getText().toString() + emojicon.getEmoji();
        mEditTextMessage.setText(text);
    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard();
        super.onBackPressed();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(final SignalREvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addMessage(event.mMessage);
            }
        });
    }

    /**
     * First of all we add message to recycler view {@link ChatActivity#mRecyclerViewChat},
     * then send to Web api {@link ApiHelper#writeMessage(Message, Callback)}
     * if happened some error we delete message from recycler view @{@link ChatActivity#mRecyclerViewChat}.
     */
    private void writeMessage() {
        ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(), new MyCallback<UserBase>() {
            @Override
            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                final UserBase sender = response.body();
                ApiHelper.getUserById(mRecipientId, new MyCallback<UserBase>() {
                    @Override
                    public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            final UserBase recipient = response.body();

                            final Message message = new Message(0, mEditTextMessage.getText().toString(), sender,
                                    recipient, new Date(System.currentTimeMillis()));

                            ApiHelper.writeMessage(message, new MyCallback<Message>() {
                                @Override
                                public void onResponse(Call<Message> call, Response<Message> response) {
                                    if (response.isSuccessful()) {
                                        if (!message.getRecipient().isReceiveOnlyFriendsMessages() &&
                                                EventBus.getDefault().hasSubscriberForEvent(
                                                        UserProfileActivity.UserUpdateEvent.class)) {
                                            EventBus.getDefault().post(new UserProfileActivity.UserUpdateEvent());
                                        }
                                        mEditTextMessage.setText("");
                                    }

                                    super.onResponse(call, response);
                                }
                            });
                        }

                        super.onResponse(call, response);
                    }
                });

                super.onResponse(call, response);
            }
        });

    }

    /**
     * The method sets recipient title(first and last name).
     */
    private void setRecipientTitle() {
        ApiHelper.getUserById(mRecipientId, new MyCallback<UserBase>() {
            @Override
            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final UserBase userBase = response.body();
                    setTitle(userBase.getFirstAndLastName());
                }

                super.onResponse(call, response);
            }
        });
    }

    /**
     * The method clears and adds messages to {@link #mMessageAdapter}.
     */
    private void updateMessages() {
        ApiHelper.getDialogue(MyApplication.getApplication().getLoggedUserId(), mRecipientId,
                new MyCallback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mMessageAdapter.clear();
                            mMessages = response.body();
                            mMessageAdapter.add(mMessages);
                            scrollMessagesToBottom();
                        }

                        super.onResponse(call, response);
                    }
                });
    }

    /**
     * The method scrolls recyclerView to bottom {@link ChatActivity#mRecyclerViewChat}
     */
    private void scrollMessagesToBottom() {
        if (mMessageAdapter.getItemCount() > 1) {
            mRecyclerViewChat.getLayoutManager().smoothScrollToPosition(mRecyclerViewChat, null,
                    mMessageAdapter.getItemCount() - 1);
        }
    }

    private void addMessage(final Message message) {
        SoundNotificationHelper.playSound(ChatActivity.this);
        VibratorServiceHelper.vibrate(ChatActivity.this,
                VibratorServiceHelper.DEFAULT_DURATION);
        mMessages.add(message);
        mMessageAdapter.add(message);
        scrollMessagesToBottom();
    }

    /**
     * The method validates @{@link #mEditTextMessage}
     *
     * @return true if data is valid
     */
    private boolean validateMessage() {
        final String message = mEditTextMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            GlideLoader.loadImage(this, mImageViewSend, R.drawable.ic_mic);
            return false;
        }

        GlideLoader.loadImage(this, mImageViewSend, R.drawable.ic_send);
        return true;
    }

    /**
     * Method shows google speech input dialog
     */
    private void promptSpeechInput() {
        final Intent intent = getRecognizeSpeechIntent();
        try {
            startActivityForResult(intent, Constants.REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, R.string.error_speech_not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideEmojicons() {
        GlideLoader.loadImage(ChatActivity.this, mImageViewSwitcher, R.drawable.ic_smile);
        mEmojicons.animate()
                .translationY(mEmojicons.getHeight())
                .alpha(0.0f)
                .setDuration(10)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mEmojicons.setVisibility(View.GONE);
                        showSoftKeyboard(mEditTextMessage);
                        requestFocus(mEditTextMessage);
                    }
                });
    }

    private void showEmojicons() {
        hideSoftKeyboard();
        GlideLoader.loadImage(ChatActivity.this, mImageViewSwitcher, R.drawable.ic_keyboard);
        mEmojicons.animate()
                .translationY(mEmojicons.getHeight())
                .alpha(1.0f)
                .setDuration(10)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mEmojicons.animate().translationY(0);
                        mEmojicons.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * Event for updating dialogues
     */
    public static class SignalREvent {
        final Message mMessage;

        public SignalREvent(Message message) {
            mMessage = message;
        }
    }

    private class TextValidator implements TextWatcher {

        private final View mView;

        private TextValidator(View view) {
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (mView.getId()) {
                case R.id.edit_text_message:
                    validateMessage();
                    break;
            }
        }
    }
}
