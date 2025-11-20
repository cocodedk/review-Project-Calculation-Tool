package com.aljamour.pkveksamen.Model;

public enum ProjectStatus {

    PLANLAGT("Planlagt"),
    I_Gang("i gang"),
    AFSLUTTET("Afsluttet"),
    ANNULERET("Annuleret");

    private final String displayName;

    ProjectStatus(String displayName){
        this.displayName = displayName;
    }

   public String getDisplayName(){
        return displayName;
   }

}
