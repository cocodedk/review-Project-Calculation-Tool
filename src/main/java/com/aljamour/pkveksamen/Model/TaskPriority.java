package com.aljamour.pkveksamen.Model;

public enum TaskPriority {
    LAV("Lav"),
    MEDIUM("Medium"),
    HØJ ("Høj"),
    KRITISK("Kritisk");

    private final String displayName;


    TaskPriority(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName(){
        return displayName;
    }
}
