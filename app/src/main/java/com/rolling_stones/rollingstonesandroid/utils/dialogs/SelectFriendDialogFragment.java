package com.rolling_stones.rollingstonesandroid.utils.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.activity.GroupSettingsActivity;
import com.rolling_stones.rollingstonesandroid.adapter.SelectUsersAdapter;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.fragments.MessagesFragment;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.RecyclerTouchListener;
import com.rolling_stones.rollingstonesandroid.utils.SpaceItemDecoration;
import com.rolling_stones.rollingstonesandroid.views.RecyclerViewEmptySupport;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SelectFriendDialogFragment extends DialogFragment {

    private static final String KEY_SELECTION_MODE = "KEY_SELECTION_MODE";

    public static final int SINGLE_SELECTION_MODE = 101;
    public static final int MULTI_SELECTION_MODE = 202;

    private SelectUsersAdapter mAdapter;
    private int mSelectionMode;

    private TextInputEditText mEditText;
    private TextInputLayout mInputLayout;

    public static DialogFragment newInstance(int selectionMode) {
        final DialogFragment dialogFragment = new SelectFriendDialogFragment();
        final Bundle args = new Bundle();

        args.putInt(KEY_SELECTION_MODE, selectionMode);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectionMode = getArguments().getInt(KEY_SELECTION_MODE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.content_select_friends, null);
        final RecyclerViewEmptySupport recyclerView = (RecyclerViewEmptySupport)
                view.findViewById(R.id.recycler_view_users);
        mInputLayout = (TextInputLayout)
                view.findViewById(R.id.text_input_layout_group_title);
        mEditText = (TextInputEditText)
                view.findViewById(R.id.text_input_edit_text_group_title);

        mInputLayout.setVisibility(View.GONE);

        if (mSelectionMode == MULTI_SELECTION_MODE) {
            mInputLayout.setVisibility(View.VISIBLE);
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateGroupTitle();
            }
        });

        ApiHelper.getFriends(MyApplication.getApplication().getLoggedUserId(),
                new Callback<List<UserBase>>() {
                    @Override
                    public void onResponse(Call<List<UserBase>> call, Response<List<UserBase>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mAdapter = new SelectUsersAdapter(response.body());
                            recyclerView.setEmptyView(view.findViewById(R.id.empty_view));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserBase>> call, Throwable t) {
                        Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
                new com.rolling_stones.rollingstonesandroid.utils.OnClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        switch (mSelectionMode) {
                            case SINGLE_SELECTION_MODE:
                                mAdapter.singleSelection(position);

                                break;

                            case MULTI_SELECTION_MODE:
                                mAdapter.toggleSelection(position);

                                break;
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }, recyclerView));
        recyclerView.addItemDecoration(SpaceItemDecoration.getDefaultSpaceItemDecoration(getContext()));

        final Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.text_button_ok, null)
                .setNegativeButton(R.string.text_button_cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mAdapter.getSelectedItemCount() <= 0) {
                            Toast.makeText(getContext(), getString(R.string.text_select_friend),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if ((mSelectionMode == MULTI_SELECTION_MODE && validateGroupTitle()) ||
                                    mSelectionMode == SINGLE_SELECTION_MODE) {
                                if (EventBus.getDefault()
                                        .hasSubscriberForEvent(
                                                GroupSettingsActivity.SelectFriendEvent.class)) {
                                    final GroupSettingsActivity.SelectFriendEvent groupSettingsEvent =
                                            new GroupSettingsActivity.SelectFriendEvent(mAdapter.getSelectedIds());
                                    EventBus.getDefault().post(groupSettingsEvent);
                                } else {
                                    final MessagesFragment.SelectFriendEvent selectFriendEvent =
                                            new MessagesFragment.SelectFriendEvent(mAdapter.getSelectedIds(),
                                                    mEditText.getText().toString());
                                    EventBus.getDefault().post(selectFriendEvent);
                                }

                                dismiss();
                            }
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private boolean validateGroupTitle() {
        final String firstName = mEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            mInputLayout.setError(getString(R.string.error_group_title_is_empty));
            requestFocus(mInputLayout);
            return false;
        }

        mInputLayout.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(@NonNull final View view) {
        if (view.requestFocus()) {
            getActivity()
                    .getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
