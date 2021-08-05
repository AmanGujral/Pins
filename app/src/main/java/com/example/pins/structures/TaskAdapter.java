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
import com.example.pins.models.UserModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskAdapterViewHolder> {
    List<TaskModel> taskList;
    Context context;
    ItemClickListener clickListener;
    LayoutInflater inflater;
    UserModel userInstance = UserModel.getUserInstance();
    String userFullName = "";

    public TaskAdapter(Context context, List<TaskModel> taskList, ItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.clickListener = listener;
        this.inflater = LayoutInflater.from(context);
        userFullName = userInstance.getFirstname() + " " + userInstance.getLastname();
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

        // Task Name/ Description
        holder.taskName.setText(task.getTaskName());

        // Task Assigned To
        if(task.getAssignedTo().contains(userFullName)) {
            holder.assignedTo.setText(userFullName);
        }
        else {
            holder.assignedTo.setText(task.getAssignedTo().get(0));
        }
        if(task.getAssignedTo().size() > 1) {
            holder.morePeopleTV.setVisibility(View.VISIBLE);
            String numPeople = "+" + String.valueOf(task.getAssignedTo().size() - 1);
            holder.morePeopleTV.setText(numPeople);
        }
        else {
            holder.morePeopleTV.setVisibility(View.GONE);
        }

        // Task Status
        holder.status.setText(task.getStatus());

        // Task Priority
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
        TextView morePeopleTV;

        public TaskAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.widget_task_container_taskname_tv);
            assignedTo = itemView.findViewById(R.id.widget_task_container_assignedto_tv);
            status = itemView.findViewById(R.id.widget_task_container_status_tv);
            priorityView = itemView.findViewById(R.id.widget_task_container_priority_view);
            morePeopleTV = itemView.findViewById(R.id.widget_task_container_assignedto_more_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onTaskAdapterItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onTaskAdapterItemClick(View view, int position);
    }
}
