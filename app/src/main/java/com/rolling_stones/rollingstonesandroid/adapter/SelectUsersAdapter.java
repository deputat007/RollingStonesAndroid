package com.rolling_stones.rollingstonesandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SelectUsersAdapter extends
        RecyclerView.Adapter<SelectUsersAdapter.SelectUsersViewHolder> {

    private final List<UserBase> mFriends;
    private final SparseBooleanArray mSelectedItems;

    public SelectUsersAdapter(List<UserBase> friends) {
        mFriends = friends;
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public SelectUsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new SelectUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectUsersViewHolder holder, int position) {
        final UserBase user = mFriends.get(position);

        IconUtils.setUserIconSmall(user, holder.mUserIcon);

        holder.mTextViewFirstName.setText(user.getFirstName());
        holder.mTextViewLastName.setText(user.getLastName());

        if (user.isOnline()) {
            holder.mImageViewOnlineStatus.setVisibility(View.VISIBLE);
        } else {
            holder.mImageViewOnlineStatus.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setActivated(mSelectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public void toggleSelection(int pos) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        } else {
            mSelectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void singleSelection(int pos) {
        clearSelections();

        setSelected(pos);
    }

    private void setSelected(int pos) {
        mSelectedItems.put(pos, true);
        notifyItemChanged(pos);
    }

    @SuppressWarnings("unused")
    public void clearSelection(int pos) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        }
        notifyItemChanged(pos);
    }

    private void clearSelections() {
        if (mSelectedItems.size() > 0) {
            mSelectedItems.clear();
            notifyDataSetChanged();
        }
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    private List<Integer> getSelectedItemsPositions() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    public int[] getSelectedIds() {
        final List<Integer> selectedItems = getSelectedItemsPositions();
        final int[] ids = new int[selectedItems.size()];

        for (int i = 0; i < selectedItems.size(); i++) {
            ids[i] = mFriends.get(selectedItems.get(i)).getId();
        }

        return ids;
    }

    class SelectUsersViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView mUserIcon;
        private final TextView mTextViewFirstName;
        private final TextView mTextViewLastName;
        private final ImageView mImageViewOnlineStatus;

        @SuppressWarnings("deprecation")
        SelectUsersViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setBackground(itemView.getContext()
                    .getResources()
                    .getDrawable(R.drawable.background_selector));
            itemView.setFocusableInTouchMode(true);

            mTextViewFirstName = (TextView) itemView.findViewById(R.id.text_view_first_name);
            mTextViewLastName = (TextView) itemView.findViewById(R.id.text_view_last_name);
            mUserIcon = (CircleImageView) itemView.findViewById(R.id.user_icon);
            mImageViewOnlineStatus = (ImageView) itemView.findViewById(R.id.iv_online_status);
        }
    }
}
