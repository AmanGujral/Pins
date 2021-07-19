package com.example.pins.structures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.models.TaskModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskAdapterViewHolder> {
    List<TaskModel> taskList;
    Context context;
    ItemClickListener clickListener;
    LayoutInflater inflater;

    public TaskAdapter(Context context, List<TaskModel> taskList, ItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.clickListener = listener;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @NotNull
    @Override
    public TaskAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.widget_task_container, parent, false);
        return new TaskAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TaskAdapterViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        
        holder.taskName.setText(task.getTaskName());
        holder.assignedTo.setText(task.getAssignedTo());
        holder.status.setText(task.getStatus());
        if (task.getPriority().equals(TaskModel.PRIORITY_HIGH)) {
            holder.priorityView.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            if (task.getPriority().equals(TaskModel.PRIORITY_MEDIUM)) {
                holder.priorityView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            } else {
                holder.priorityView.setBackgroundColor(context.getResources().getColor(R.color.green));
            }
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView taskName;
        TextView assignedTo;
        TextView status;
        View priorityView;

        public TaskAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.widget_task_container_taskname_tv);
            assignedTo = itemView.findViewById(R.id.widget_task_container_assignedto_tv);
            status = itemView.findViewById(R.id.widget_task_container_status_tv);
            priorityView = itemView.findViewById(R.id.widget_task_container_priority_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
