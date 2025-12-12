package com.example.pkveksamen.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SubProject {

    private long subProjectID;
    private String subProjectName;
    private String subProjectDescription;
    private int subProjectDuration;
    private LocalDate subProjectStartDate;
    private LocalDate subProjectDeadline;


    public SubProject(long subProjectID, String subProjectName, String subProjectDescription, int subProjectDuration, LocalDate subProjectDeadline, LocalDate subProjectStartDate) {
        this.subProjectID = subProjectID;
        this.subProjectName = subProjectName;
        this.subProjectDescription = subProjectDescription;
        this.subProjectDuration = subProjectDuration;
        this.subProjectDeadline = subProjectDeadline;
        this.subProjectStartDate = subProjectStartDate;
    }

    public SubProject() {

    }

    public void recalculateDuration() {
        if (subProjectStartDate != null && subProjectDeadline != null) {
            long days = ChronoUnit.DAYS.between(subProjectStartDate, subProjectDeadline);

            // Hvis I vil tælle begge dage med:
            // days = days + 1;

            if (days < 0) {
                subProjectDuration = 0;
            } else {
                subProjectDuration = (int) days;
            }
        } else {
            subProjectDuration = 0;
        }
    }


    public long getSubProjectID() {
        return subProjectID;
    }

    public void setSubProjectID(long subProjectID) {
        this.subProjectID = subProjectID;
    }

    public String getSubProjectName() {
        return subProjectName;
    }

    public void setSubProjectName(String subProjectName) {
        this.subProjectName = subProjectName;
    }

    public String getSubProjectDescription() {
        return subProjectDescription;
    }

    public void setSubProjectDescription(String subProjectDescription) {
        this.subProjectDescription = subProjectDescription;
    }


    public int getSubProjectDuration() {
        return subProjectDuration;
    }

    public void setSubProjectDuration(int subProjectDuration) {
        this.subProjectDuration = subProjectDuration;
    }

    public LocalDate getSubProjectStartDate() {
        return subProjectStartDate;
    }

    public void setSubProjectStartDate(LocalDate subProjectStartDate) {
        this.subProjectStartDate = subProjectStartDate;
    }

    public LocalDate getSubProjectDeadline() {
        return subProjectDeadline;
    }

    public void setSubProjectDeadline(LocalDate subProjectDeadline) {
        this.subProjectDeadline = subProjectDeadline;
    }

}
