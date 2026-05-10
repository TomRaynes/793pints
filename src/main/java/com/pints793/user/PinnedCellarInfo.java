package com.pints793.user;

public class PinnedCellarInfo {

    private String cellarId;
    private String cellarName;
    private String organisationId;
    private String organisationName;

    public String getCellarId() {
        return cellarId;
    }

    public PinnedCellarInfo setCellarId(String cellarId) {
        this.cellarId = cellarId;
        return this;
    }

    public String getCellarName() {
        return cellarName;
    }

    public PinnedCellarInfo setCellarName(String cellarName) {
        this.cellarName = cellarName;
        return this;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public PinnedCellarInfo setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
        return this;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public PinnedCellarInfo setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        return this;
    }
}

