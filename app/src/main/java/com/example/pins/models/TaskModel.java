package com.example.pins.models;

public class TaskModel {
    private String taskId;
    private String taskName;
    private String assignedTo;
    private String status;
    private String priority;

    public static final String STATUS_TODO = "To Do";
    public static final String STATUS_DOING = "Doing";
    public static final String STATUS_DONE = "Done";

    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_MEDIUM = "Medium";
    public static final String PRIORITY_HIGH = "High";

    public TaskModel() {}

    public TaskModel(
            String taskId,
            String taskName,
            String assignedTo,
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

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
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
