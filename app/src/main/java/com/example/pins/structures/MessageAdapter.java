package com.example.pins.structures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.models.MessageModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder>{
    Context context;
    List<MessageModel> messageList;
    ItemClickListener clickListener;

    public MessageAdapter(Context context, List<MessageModel> messageList, ItemClickListener listener) {
        this.context = context;
        this.messageList = messageList;
        this.clickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_message_container,parent,false);

        return new MessageAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapterViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if(message.getStatus().equals(MessageModel.STATUS_SENT)) {
            holder.msgSentLayout.setVisibility(View.VISIBLE);
            holder.msgReceivedLayout.setVisibility(View.GONE);
            holder.msgSentTV.setText(message.getMessage());
            holder.msgSentTimestamp.setText(message.getTimeSent());
        }
        else {
            holder.msgSentLayout.setVisibility(View.GONE);
            holder.msgReceivedLayout.setVisibility(View.VISIBLE);
            holder.msgReceivedTV.setText(message.getMessage());
            holder.msgReceivedTimestamp.setText(message.getTimeSent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessageAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {


        LinearLayout msgSentLayout;
        LinearLayout msgReceivedLayout;
        TextView msgSentTV;
        TextView msgReceivedTV;
        TextView msgSentTimestamp;
        TextView msgReceivedTimestamp;

        public MessageAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            msgSentLayout = itemView.findViewById(R.id.widget_message_container_sent_layout);
            msgReceivedLayout = itemView.findViewById(R.id.widget_message_container_received_layout);
            msgSentTV = itemView.findViewById(R.id.widget_message_container_sent_tv);
            msgReceivedTV = itemView.findViewById(R.id.widget_message_container_received_tv);
            msgSentTimestamp = itemView.findViewById(R.id.widget_message_container_sent_timestamp);
            msgReceivedTimestamp = itemView.findViewById(R.id.widget_message_container_received_timestamp);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if(clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
            return true;
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
