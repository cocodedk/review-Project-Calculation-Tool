package com.example.pkveksamen.Model;

public enum EmployeeRole {
    // TODO: få den til at bruge attributnavnet i stedet for variabelnavnet
    PROJECT_MANAGER("Project Manager"),
    TEAM_MEMBER("Team Member");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
