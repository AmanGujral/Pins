package com.example.pins.structures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameAdapterViewHolder>{
    List<String> nameList;
    Context context;
    LayoutInflater inflater;

    public NameAdapter(Context context, List<String> nameList) {
        this.context = context;
        this.nameList = nameList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public NameAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.widget_name_container, parent, false);
        return new NameAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NameAdapterViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class NameAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        public NameAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.widget_name_container_tv);
        }
    }
}
