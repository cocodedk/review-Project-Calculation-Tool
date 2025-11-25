package com.aljamour.pkveksamen.Model;

public enum TaksStatus {
    IKKE_STARTET("ikke startet"),
    I_IGANG("I gang"),
    AFLUTTET("Afsluttet");

    private final String displayName;

    TaksStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
