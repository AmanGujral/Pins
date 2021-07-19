package com.example.pins.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.ui.message.messageactivity;
import com.google.firebase.firestore.auth.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class useradapter extends RecyclerView.Adapter {

    public TextView username;
    public  ImageView userimage;
    private Context mcontext;
    private List<User> muser;

    public  useradapter(Context mcontext,List<User> muser)
    {
        this.muser=muser;
        this.mcontext=mcontext;

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {


     View view= LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
     return new useradapter.ViewHolder(view);



    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        
        username.findViewById(R.id.profile_username);
        User user=muser.get(position);
        holder.username.setText(user.getUid());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mcontext, messageactivity.class);

                intent.putExtra("userid",user.getUid());
                mcontext.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return muser.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder
    {

      


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

       username=itemView.findViewById(R.id.name);
       userimage=itemView.findViewById(R.id.image);

        }


    }
}
