package com.rolling_stones.rollingstonesandroid.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.Message;
import com.rolling_stones.rollingstonesandroid.utils.DateFormatter;
import com.rolling_stones.rollingstonesandroid.utils.DateHelper;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.rockerhieu.emojicon.EmojiconTextView;


public class DialoguesAdapter extends RecyclerView.Adapter<DialoguesAdapter.DialoguesViewHolder> {

    public static final Comparator<Message> DEFAULT_COMPARATOR = new Comparator<Message>() {
        @Override
        public int compare(Message o1, Message o2) {
            return o2.getDateAndTime().compareTo(o1.getDateAndTime());
        }
    };

    private final Comparator<Message> mComparator;
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
                    return item1.getId() == item2.getId();
                }
            });

    public DialoguesAdapter(Comparator<Message> comparator) {
        mComparator = comparator;
    }

    @Override
    public DialoguesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_dialogue, parent, false);

        return new DialoguesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialoguesViewHolder holder, int position) {
        final Message message = mSortedList.get(position);

        int loggedUserId = MyApplication.getApplication().getLoggedUserId();

        if (message.getGroup() == null) {
            if (message.getRecipient().getId() == loggedUserId) {

                holder.mSenderIcon.setVisibility(View.GONE);

                IconUtils.setUserIconSmall(message.getSender(), holder.mUserIcon);
            } else {
                holder.mSenderIcon.setVisibility(View.VISIBLE);

                IconUtils.setUserIconSmall(message.getSender(), holder.mSenderIcon);
                IconUtils.setUserIconSmall(message.getRecipient(), holder.mUserIcon);
            }
        } else {
            IconUtils.setGroupIconSmall(message.getGroup(), holder.mUserIcon);
        }

        if (message.getSender().getId() == loggedUserId || message.getGroup() != null) {
            holder.mSenderIcon.setVisibility(View.VISIBLE);

            IconUtils.setUserIconSmall(message.getSender(), holder.mSenderIcon);

            if (message.getGroup() == null) {
                holder.mTextViewTitle.setText(message.getRecipient().getFirstAndLastName());
            } else {
                holder.mTextViewTitle.setText(message.getGroup().getName());
            }
        } else {
            holder.mSenderIcon.setVisibility(View.GONE);

            if (message.getGroup() == null) {
                holder.mTextViewTitle.setText(message.getSender().getFirstAndLastName());
            } else {
                holder.mTextViewTitle.setText(message.getGroup().getName());
            }
        }

        if (DateHelper.isSameDay(message.getDateAndTime(), new Date(System.currentTimeMillis()))) {
            holder.mTextViewDateOrTime.setText(DateFormatter.parseDate(message.getDateAndTime(),
                    holder.mTextViewDateOrTime.getContext(), DateFormatter.TIME_PATTERN));
        } else {
            holder.mTextViewDateOrTime.setText(DateFormatter.parseDate(message.getDateAndTime(),
                    holder.mTextViewDateOrTime.getContext(), DateFormatter.DAY_PATTERN));
        }

        holder.mTextViewLastMessage.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public void add(@NonNull final Message model) {
        mSortedList.add(model);
    }

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

    class DialoguesViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView mUserIcon;
        private final CircleImageView mSenderIcon;
        private final TextView mTextViewTitle;
        private final EmojiconTextView mTextViewLastMessage;
        private final TextView mTextViewDateOrTime;

        DialoguesViewHolder(View itemView) {
            super(itemView);

            mUserIcon = (CircleImageView) itemView.findViewById(R.id.user_icon);
            mSenderIcon = (CircleImageView) itemView.findViewById(R.id.user_icon_sender);
            mTextViewTitle = (TextView) itemView.findViewById(R.id.text_view_header);
            mTextViewLastMessage = (EmojiconTextView) itemView.findViewById(R.id.text_view_last_message);
            mTextViewDateOrTime = (TextView) itemView.findViewById(R.id.text_view_date_or_time);
        }
    }
}
