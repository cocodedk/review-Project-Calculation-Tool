package com.aljamour.pkveksamen.Model;

public class SubTask {
    private long subTaskID;
    private String subTaskName;
    private String subTaskDescription;
    private String subTaskDuration;


    public SubTask(long subTaskID, String subTaskName, String subTaskDescription, String subTaskDuration) {
        this.subTaskID = subTaskID;
        this.subTaskName = subTaskName;
        this.subTaskDescription = subTaskDescription;
        this.subTaskDuration = subTaskDuration;
    }

    public long getSubTaskID() {
        return subTaskID;
    }

    public void setSubTaskID(long subTaskID) {
        this.subTaskID = subTaskID;
    }

    public String getSubTaskName() {
        return subTaskName;
    }

    public void setSubTaskName(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    public String getSubTaskDescription() {
        return subTaskDescription;
    }

    public void setSubTaskDescription(String subTaskDescription) {
        this.subTaskDescription = subTaskDescription;
    }

    public String getSubTaskDuration() {
        return subTaskDuration;
    }

    public void setSubTaskDuration(String subTaskDuration) {
        this.subTaskDuration = subTaskDuration;
    }
}
