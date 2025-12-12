package com.example.pkveksamen.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Project {

    private long projectID;
    private String projectName;
    private String projectDescription;
    private LocalDate projectStartDate;
    private LocalDate projectDeadline;
    private String projectCustomer;
    private int projectDuration;


    public void recalculateDuration() {
        if (projectStartDate != null && projectDeadline != null) {
            long days = ChronoUnit.DAYS.between(projectStartDate, projectDeadline);

            // Hvis I vil tælle begge dage med:
            // days = days + 1;

            if (days < 0) {
                projectDuration = 0;
            } else {
                projectDuration = (int) days;
            }
        } else {
            projectDuration = 0;
        }
    }


    public Project(){}

    public Project(long projectID, String projectName, String projectDescription, LocalDate projectStartDate, LocalDate projectDeadline,
                   String projectCustomer, int projectDuration) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = projectStartDate;
        this.projectDeadline = projectDeadline;
        this.projectCustomer = projectCustomer;
        this.projectDuration = projectDuration;

    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public LocalDate getProjectDeadline() {
        return projectDeadline;
    }

    public void setProjectDeadline(LocalDate projectDeadline) {
        this.projectDeadline = projectDeadline;
    }

    public String getProjectCustomer() {
        return projectCustomer;
    }

    public void setProjectCustomer(String projectCustomer) {
        this.projectCustomer = projectCustomer;
    }

    public int getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(int projectDuration) {
        this.projectDuration = projectDuration;
    }

}
