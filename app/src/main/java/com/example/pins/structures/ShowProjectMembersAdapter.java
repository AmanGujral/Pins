package com.example.pins.structures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.models.ProjectMemberModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShowProjectMembersAdapter extends RecyclerView.Adapter<ShowProjectMembersAdapter.ShowProjectMembersViewHolder>{
    List<ProjectMemberModel> nameList;
    Context context;
    Boolean showRemoveBtn = false;
    LayoutInflater inflater;
    ItemClickListener clickListener;

    public ShowProjectMembersAdapter(Context context, List<ProjectMemberModel> nameList, Boolean showRemoveBtn, ItemClickListener listener) {
        this.context = context;
        this.nameList = nameList;
        this.showRemoveBtn = showRemoveBtn;
        this.clickListener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public ShowProjectMembersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.widget_name_container_large, parent, false);
        return new ShowProjectMembersAdapter.ShowProjectMembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ShowProjectMembersViewHolder holder, int position) {
        String fullName = nameList.get(position).getFirstname() + " " + nameList.get(position).getLastname();
        holder.name.setText(fullName);
        if(showRemoveBtn) {
            holder.removeBtn.setVisibility(View.VISIBLE);
            holder.addBtn.setVisibility(View.GONE);
        }
        else {
            holder.removeBtn.setVisibility(View.GONE);
            holder.addBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ShowProjectMembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        ImageButton removeBtn;
        ImageButton addBtn;

        public ShowProjectMembersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.widget_name_container_large_tv);
            removeBtn = itemView.findViewById(R.id.widget_name_container_large_remove_btn);
            addBtn = itemView.findViewById(R.id.widget_name_container_large_add_btn);

            removeBtn.setOnClickListener(this);
            addBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onShowProjectMembersAdapterItemClick(view, getAdapterPosition(), showRemoveBtn);
            }
        }
    }

    public interface ItemClickListener {
        void onShowProjectMembersAdapterItemClick(View view, int position, Boolean showRemoveBtn);
    }
}
