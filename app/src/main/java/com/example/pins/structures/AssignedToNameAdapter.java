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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AssignedToNameAdapter extends RecyclerView.Adapter<AssignedToNameAdapter.NameAdapterViewHolder>{
    List<String> nameList;
    Context context;
    Boolean editable = false;
    LayoutInflater inflater;
    ItemClickListener clickListener;

    public AssignedToNameAdapter(Context context, List<String> nameList, Boolean editable, ItemClickListener listener) {
        this.context = context;
        this.nameList = nameList;
        this.editable = editable;
        this.clickListener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public NameAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.widget_name_container_small, parent, false);
        return new NameAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NameAdapterViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        if(editable) {
            holder.removeBtn.setVisibility(View.VISIBLE);
        }
        else {
            holder.removeBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class NameAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        ImageButton removeBtn;

        public NameAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.widget_name_container_tv);
            removeBtn = itemView.findViewById(R.id.widget_name_container_remove_btn);

            removeBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onAssignedToNameAdapterItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onAssignedToNameAdapterItemClick(View view, int position);
    }
}
