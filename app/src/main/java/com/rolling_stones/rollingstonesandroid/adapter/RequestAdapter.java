package com.rolling_stones.rollingstonesandroid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.models.UserBaseComparators;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Comparator<UserBase> mComparator;
    private final SortedList<UserBase> mSortedList = new SortedList<>(UserBase.class,
            new SortedList.Callback<UserBase>() {
                @Override
                public int compare(UserBase o1, UserBase o2) {
                    return mComparator.compare(o1, o2);
                }

                @Override
                public void onInserted(int position, int count) {
                    notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onChanged(int position, int count) {
                    notifyItemRangeChanged(position, count);
                }

                @Override
                public boolean areContentsTheSame(UserBase oldItem, UserBase newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areItemsTheSame(UserBase item1, UserBase item2) {
                    return item1.getId() == item2.getId();
                }
            });

    private OnClickListener mListener;

    public RequestAdapter(@NonNull final OnClickListener listener) {
        mListener = listener;
        updateComparator();
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        final UserBase user = mSortedList.get(position);

        IconUtils.setUserIconSmall(user, holder.mUserIcon);

        holder.mTextViewFirstName.setText(user.getFirstName());
        holder.mTextViewLastName.setText(user.getLastName());
        if (user.isOnline()) {
            holder.mImageViewOnlineStatus.setVisibility(View.VISIBLE);
        } else {
            holder.mImageViewOnlineStatus.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public void add(@NonNull final UserBase model) {
        mSortedList.add(model);
    }

    public void remove(@NonNull final UserBase model) {
        mSortedList.remove(model);
    }

    public void add(@NonNull final List<UserBase> models) {
        mSortedList.addAll(models);
    }

    @SuppressWarnings("unused")
    public void remove(@NonNull final List<UserBase> models) {
        mSortedList.beginBatchedUpdates();

        for (UserBase model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void clear() {
        mSortedList.beginBatchedUpdates();
        mSortedList.clear();
        mSortedList.endBatchedUpdates();
        updateComparator();
    }

    public void replaceAll(@NonNull final List<UserBase> models) {
        mSortedList.beginBatchedUpdates();

        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final UserBase model = mSortedList.get(i);

            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }

        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    public UserBase getItem(int position) {
        return mSortedList.get(position);
    }

    private void updateComparator() {
        mComparator = UserBaseComparators.getComparatorByType(
                MyApplication.getApplication().getSharedPrefHelper().getListOrder());
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView mUserIcon;
        private final TextView mTextViewFirstName;
        private final TextView mTextViewLastName;
        private final Button mButtonAccept;
        private final Button mButtonRemove;
        private final ImageView mImageViewOnlineStatus;

        RequestViewHolder(View itemView) {
            super(itemView);

            mTextViewFirstName = (TextView) itemView.findViewById(R.id.text_view_first_name);
            mTextViewLastName = (TextView) itemView.findViewById(R.id.text_view_last_name);
            mUserIcon = (CircleImageView) itemView.findViewById(R.id.user_icon);
            mButtonAccept = (Button) itemView.findViewById(R.id.button_accept);
            mButtonRemove = (Button) itemView.findViewById(R.id.button_remove);
            mImageViewOnlineStatus = (ImageView) itemView.findViewById(R.id.iv_online_status);

            mButtonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAcceptClicked(getAdapterPosition(), v);
                }
            });
            mButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRemoveClicked(getAdapterPosition(), v);
                }
            });
        }
    }

    public interface OnClickListener {
        void onAcceptClicked(int position, @NonNull final View v);

        void onRemoveClicked(int position, @NonNull final View v);
    }
}
