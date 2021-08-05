package com.example.pins.structures;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SidePanelProjectAdapter extends RecyclerView.Adapter<SidePanelProjectAdapter.SidePanelViewHolder> {

    List<ProjectModel> projectList = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    ItemClickListener clickListener;
    UserModel userInstance = UserModel.getUserInstance();

    public SidePanelProjectAdapter(Context context, List<ProjectModel> list, ItemClickListener listener) {
        this.context = context;
        this.projectList = list;
        this.clickListener = listener;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @NotNull
    @Override
    public SidePanelViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.widget_side_panel_project_container, parent, false);
        return new SidePanelProjectAdapter.SidePanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SidePanelViewHolder holder, int position) {
        if(userInstance.getCurrentProjectId().equals(projectList.get(position).getProjectId())) {
            holder.projectInitials.setBackground(context.getDrawable(R.drawable.green_border));
            holder.projectInitials.setTextColor(context.getResources().getColor(R.color.green));
        }
        holder.projectInitials.setText(getInitials(projectList.get(position).getProjectName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.projectInitials.setTooltipText(projectList.get(position).getProjectName());
        }

    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    class SidePanelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView projectInitials;

        public SidePanelViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            projectInitials = itemView.findViewById(R.id.widget_side_panel_project_container_project_initials_tv);

            projectInitials.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onSidePanelProjectAdapterItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onSidePanelProjectAdapterItemClick(View view, int position);
    }

    public String getInitials(String name) {
        String initials = "";
        if(name.split(" ").length == 1) {
            initials = name.toUpperCase().substring(0, 1);
        }
        else if(name.split(" ").length > 1){
            for(int i = 0; i < 2; i++) {
                initials = initials.concat(name.split(" ")[i].toUpperCase().substring(0, 1));
            }
        }

        return initials;
    }
}
