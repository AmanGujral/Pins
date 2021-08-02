package com.example.pins.models;

import java.util.List;

public class TaskModel {
    private String taskId;
    private String taskName;
    private List<String> assignedTo;
    private String status;
    private String priority;

    public static final String STATUS_TODO = "To Do";
    public static final String STATUS_DOING = "Doing";
    public static final String STATUS_DONE = "Done";

    public static final String PRIORITY_LOW = "3";
    public static final String PRIORITY_MEDIUM = "2";
    public static final String PRIORITY_HIGH = "1";

    public TaskModel() {}

    public TaskModel(
            String taskId,
            String taskName,
            List<String> assignedTo,
            String status,
            String priority
    ) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;

        if(status == null)
            this.status = STATUS_TODO;
        else
            this.status = status;

        if(priority == null)
            this.priority = PRIORITY_LOW;
        else
            this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(List<String> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
