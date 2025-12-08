package com.example.pkveksamen.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task {
    private int taskID;
    private String taskName;
    private String taskDescription;
    private int taskDuration;
    private String taskNote;
    private LocalDate taskStartDate;
    private LocalDate taskDeadline;
    private Priority priority;
    private Status status;
    private EmployeeRole employeeRole;

    public Task(){}

    public Task(int taskID, String taskName, String taskDescription, Status status, int taskDuration,
                String taskNote, LocalDate taskStartDate, LocalDate taskDeadline, Priority priority, EmployeeRole employeeRole) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
        this.taskDuration = taskDuration;
        this.taskNote = taskNote;
        this.taskStartDate = taskStartDate;
        this.taskDeadline = taskDeadline;
        this.priority = priority;
        this.employeeRole = employeeRole;
    }

    public void recalculateDuration() {
        if (taskStartDate != null && taskDeadline != null) {
            long days = ChronoUnit.DAYS.between(taskStartDate, taskDeadline);

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

    public Status getTaskStatus() {
        return status;
    }

    public void setTaskStatus(Status status) {
        this.status = status;
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

    public LocalDate getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(LocalDate taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public LocalDate getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(LocalDate taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public Priority getTaskPriority() {
        return priority;
    }

    public void setTaskPriority(Priority priority) {
        this.priority = priority;
    }


}
