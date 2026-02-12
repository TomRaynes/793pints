package com.pints793.organisation;

public class RenameOrganisationRequest {

    private String oldName;
    private String newName;

    public String getOldName() {
        return  oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return  newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
