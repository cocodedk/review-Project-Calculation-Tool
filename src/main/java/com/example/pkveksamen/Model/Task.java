package com.example.pkveksamen.Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task {
    private int taskID;
    private String taskName;
    private String taskDescription;
    private int taskDuration;
    private String taskNote;
    private LocalDate startDate;
    private LocalDate endDate;
    private TaskPriority taskPriority;
    private TaskStatus taskStatus;


    public Task(){}

    public Task(int taskID, String taskName, String taskDescription, TaskStatus taskStatus, int taskDuration,
                String taskNote, LocalDate startDate, LocalDate endDate, TaskPriority taskPriority) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskDuration = taskDuration;
        this.taskNote = taskNote;
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskPriority = taskPriority;

    }


    public void recalculateDuration() {
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);

            if (days < 0) {
                taskDuration = 0;
            } else {
                taskDuration = (int) days;
            }
        } else {
            taskDuration = 0;
        }
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(int taskDuration) {
        this.taskDuration = taskDuration;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }


}
