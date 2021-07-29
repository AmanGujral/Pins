package com.example.pins.models;

public class TasksModel {
    private String taskId;
    private String taskName;
    private String assignedTo;

    public TasksModel(){

    }

    public TasksModel(String taskId, String taskName, String assignedTo) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
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
}
