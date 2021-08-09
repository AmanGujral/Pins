package com.example.pins.structures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pins.R;
import com.example.pins.models.ContactModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContactAdapter extends  RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    private final Context context;
    List<ContactModel> contactList;
    ItemClickListener clickListener;

    public ContactAdapter(Context context, List<ContactModel> contactList, ItemClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.clickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ContactAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_contact_container,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactAdapter.MyViewHolder holder, int position) {
        ContactModel contact = contactList.get(position);
        holder.firstName.setText(contact.getFirstname());
        holder.lastName.setText(contact.getLastname());
        if(!contact.getLastMsg().isEmpty()) {
            holder.lastMsg.setText(contact.getLastMsg());
        }
        else {
            holder.lastMsg.setText(R.string.say_hello);
            holder.lastMsg.setTextColor(context.getResources().getColor(R.color.green));
        }
        holder.lastMsgTime.setText(contact.getLastMsgTime());

        if(!contact.getImageUrl().isEmpty()) {
            holder.initialsTV.setVisibility(View.GONE);
            holder.profilePic.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(contact.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile_24)
                    .into(holder.profilePic);
        }
        else {
            holder.initialsTV.setVisibility(View.VISIBLE);
            holder.profilePic.setVisibility(View.GONE);

            holder.initialsTV.setText(String.valueOf(contact.getFirstname().trim().toUpperCase().charAt(0)));
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView firstName;
        TextView lastName;
        TextView lastMsg;
        TextView lastMsgTime;
        TextView initialsTV;
        ImageView profilePic;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            firstName = itemView.findViewById(R.id.widget_contact_container_firstname_tv);
            lastName = itemView.findViewById(R.id.widget_contact_container_lastname_tv);
            lastMsg = itemView.findViewById(R.id.widget_contact_container_lastmsg_tv);
            lastMsgTime = itemView.findViewById(R.id.widget_contact_container_lastmsgtime_tv);
            initialsTV = itemView.findViewById(R.id.widget_contact_container_pic_textview);
            profilePic = itemView.findViewById(R.id.widget_contact_container_pic_imageview);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onContactAdapterItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onContactAdapterItemClick(View view, int position);
    }
}
