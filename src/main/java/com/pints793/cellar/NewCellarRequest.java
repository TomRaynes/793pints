package com.pints793.cellar;

public class NewCellarRequest {

    private String cellarName;
    private String organisationId;

    public String getCellarName() {
        return cellarName;
    }

    public void setCellarName(String cellarId) {
        this.cellarName = cellarId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }
}
