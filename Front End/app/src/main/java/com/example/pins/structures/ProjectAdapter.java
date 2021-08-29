package com.example.pins.structures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.models.ProjectModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    Context context;
    List<ProjectModel> projectList;
    LayoutInflater inflater;
    private ItemClickListener clickListener;

    public ProjectAdapter(Context context, List<ProjectModel> list, ItemClickListener listener) {
        this.context = context;
        this.projectList = list;
        this.clickListener = listener;
        this.inflater = (LayoutInflater.from(context));
    }

    @NonNull
    @NotNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.widget_project_container, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProjectViewHolder holder, int position) {
        holder.projectCode.setText(projectList.get(position).getProjectCode());
        holder.projectName.setText(projectList.get(position).getProjectName());
        holder.projectManager.setText(projectList.get(position).getManagerName());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }


    class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView projectCode;
        TextView projectName;
        TextView projectManager;

        public ProjectViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            projectCode = itemView.findViewById(R.id.widget_project_container_projectcode_tv);
            projectName = itemView.findViewById(R.id.widget_project_container_projectname_tv);
            projectManager = itemView.findViewById(R.id.widget_project_container_projectmanager_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onProjectAdapterItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onProjectAdapterItemClick(View view, int position);
    }
}
