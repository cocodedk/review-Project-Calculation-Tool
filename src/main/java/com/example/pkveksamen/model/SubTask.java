package com.example.pkveksamen.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SubTask {
    private long subTaskId;
    private String subTaskName;
    private String subTaskDescription;
    private int subTaskDuration;
    private Status subTaskStatus;
    private Priority subTaskPriority;
    private String subTaskNote;
    private LocalDate subTaskStartDate;
    private LocalDate subTaskDeadline;

    public SubTask(){}

    public SubTask(long subTaskId, String subTaskName, String subTaskDescription, int subTaskDuration, Status subTaskStatus,
                   Priority subTaskPriority, String subTaskNote, LocalDate subTaskStartDate, LocalDate subTaskDeadline) {
        this.subTaskId = subTaskId;
        this.subTaskName = subTaskName;
        this.subTaskDescription = subTaskDescription;
        this.subTaskDuration = subTaskDuration;
        this.subTaskStatus = subTaskStatus;
        this.subTaskPriority = subTaskPriority;
        this.subTaskNote = subTaskNote;
        this.subTaskStartDate = subTaskStartDate;
        this.subTaskDeadline = subTaskDeadline;
    }

    public void recalculateDuration() {
        if (subTaskStartDate != null && subTaskDeadline != null) {
            long days = ChronoUnit.DAYS.between(subTaskStartDate, subTaskDeadline);

            // Hvis I vil tælle begge dage med:
            // days = days + 1;

            if (days < 0) {
                subTaskDuration = 0; // eller kast exception, hvis det er “ulovligt”
            } else {
                subTaskDuration = (int) days;
            }
        } else {
            subTaskDuration = 0;
        }
    }

    public long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(long subTaskId) {
        this.subTaskId = subTaskId;
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

    public int getSubTaskDuration() {
        return subTaskDuration;
    }

    public void setSubTaskDuration(int subTaskDuration) {
        this.subTaskDuration = subTaskDuration;
    }

    public Status getSubTaskStatus() {
        return subTaskStatus;
    }

    public void setSubTaskStatus(Status subTaskStatus) {
        this.subTaskStatus = subTaskStatus;
    }

    public Priority getSubTaskPriority() {
        return subTaskPriority;
    }

    public void setSubTaskPriority(Priority subTaskPriority) {
        this.subTaskPriority = subTaskPriority;
    }

    public LocalDate getSubTaskStartDate() {
        return subTaskStartDate;
    }

    public void setSubTaskStartDate(LocalDate subTaskStartDate) {
        this.subTaskStartDate = subTaskStartDate;
    }

    public LocalDate getSubTaskDeadline() {
        return subTaskDeadline;
    }

    public void setSubTaskDeadline(LocalDate subTaskDeadline) {
        this.subTaskDeadline = subTaskDeadline;
    }

    public String getSubTaskNote() {
        return subTaskNote;
    }

    public void setSubTaskNote(String subTaskNote) {
        this.subTaskNote = subTaskNote;
    }
}