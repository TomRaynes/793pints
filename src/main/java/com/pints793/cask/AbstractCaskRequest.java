package com.pints793.cask;

public abstract class AbstractCaskRequest {

    private String cellarId;
    private String organisationId;

    public String getCellarId() {
        return cellarId;
    }

    public void setCellarId(String cellarId) {
        this.cellarId = cellarId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }
}
