package com.example.pins.structures;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.ChatActivity;
import com.example.pins.R;
import com.example.pins.models.UserMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private final Context context;
    ArrayList<UserMessage> userMessageArrayList;

    public MessageAdapter(Context context, ArrayList<UserMessage> userMessageArrayList) {
        this.context = context;
        this.userMessageArrayList = userMessageArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.messageitem,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.MyViewHolder holder, int position) {
        UserMessage userMessage = userMessageArrayList.get(position);
        holder.FirstName.setText(userMessage.getFirstname());
        holder.LastName.setText(userMessage.getLastname());

        holder.FirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userMessageArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView FirstName, LastName;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            FirstName = itemView.findViewById(R.id.FName);
            LastName = itemView.findViewById(R.id.LName);


        }

        @Override
        public void onClick(View v) {

        }
    }
}
