package com.rolling_stones.rollingstonesandroid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;
import com.rolling_stones.rollingstonesandroid.utils.FontSizeHelper;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.rockerhieu.emojicon.EmojiconTextView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final Comparator<Message> DEFAULT_COMPARATOR = new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getDateAndTime().compareTo(o2.getDateAndTime());
        }
    };

    private static final int SELF = 100;

    private final SortedList<Message> mSortedList = new SortedList<>(Message.class,
            new SortedList.Callback<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
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
                public boolean areContentsTheSame(Message oldItem, Message newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areItemsTheSame(Message item1, Message item2) {
                    return item1 == item2;
                }
            });
    private final Comparator<Message> mComparator;
    private final int mUserId;
    private final OnUserIconClickListener mListener;

    public MessageAdapter(Comparator<Message> comparator, int userId,
                          OnUserIconClickListener listener) {
        mComparator = comparator;
        mUserId = userId;
        mListener = listener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == SELF) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_message_chat_in, parent, false);
        } else {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_message_chat_out, parent, false);
        }

        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        final Message message = mSortedList.get(position);

        if (holder.mViewType != SELF) {
            IconUtils.setUserIconSmall(message.getSender(), holder.mUserIcon);
            if (message.getSender().isOnline()) {
                holder.mImageViewOnlineStatus.setVisibility(View.VISIBLE);
            } else {
                holder.mImageViewOnlineStatus.setVisibility(View.INVISIBLE);
            }
        }

        holder.mTextViewDateTime.setText(DateFormatter.parseDate(message.getDateAndTime(),
                holder.mTextViewDateTime.getContext(), DateFormatter.DATE_WITH_TIME_PATTERN));
        holder.mTextViewMessage.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        final Message message = mSortedList.get(position);

        if (message.getSender() != null && message.getSender().getId() == mUserId) {
            return SELF;
        }

        return position;
    }

    public void add(@NonNull final Message model) {
        mSortedList.add(model);
    }

    @SuppressWarnings("unused")
    public void remove(@NonNull final Message model) {
        mSortedList.remove(model);
    }

    public void add(@NonNull final List<Message> models) {
        mSortedList.addAll(models);
    }

    @SuppressWarnings("unused")
    public void remove(@NonNull final List<Message> models) {
        mSortedList.beginBatchedUpdates();

        for (Message model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void clear() {
        mSortedList.beginBatchedUpdates();
        mSortedList.clear();
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(@NonNull final List<Message> models) {
        mSortedList.beginBatchedUpdates();

        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Message model = mSortedList.get(i);

            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }

        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    public Message getItem(int position) {
        return mSortedList.get(position);
    }

    public void filter(@NonNull final List<Message> models, @NonNull final String query) {

        final List<Message> filteredModelList = new ArrayList<>();

        for (Message model : models) {
            final String message = model.getText().toLowerCase();

            if (message.contains(query.trim())) {
                filteredModelList.add(model);
            }
        }

        replaceAll(filteredModelList);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mUserIcon;
        private final EmojiconTextView mTextViewMessage;
        private final TextView mTextViewDateTime;
        private final int mViewType;
        private final ImageView mImageViewOnlineStatus;

        MessageViewHolder(View itemView, int viewType) {
            super(itemView);

            mViewType = viewType;

            if (viewType != SELF) {
                mUserIcon = (CircleImageView) itemView.findViewById(R.id.user_icon);
                mUserIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(v, getAdapterPosition());
                    }
                });
            }

            mTextViewDateTime = (TextView) itemView.findViewById(R.id.text_view_date_time);
            mTextViewMessage = (EmojiconTextView) itemView.findViewById(R.id.text_view_message);
            mImageViewOnlineStatus = (ImageView) itemView.findViewById(R.id.iv_online_status);

            mTextViewMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    FontSizeHelper.getFontSize(mTextViewMessage.getContext()));
        }
    }

    public interface OnUserIconClickListener {
        void onClick(@NonNull final View view, int position);
    }
}
