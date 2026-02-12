package com.pints793.cellar;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Cellar-Management")
public class Cellar {

    @Id
    private String id;
    private String organisationId;
    private String name;
    private List<Cask> casks;

    public Cellar() {
        casks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Cellar setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public Cellar setId(String id) {
        this.id = id;
        return this;
    }

    public Cellar setCasks(List<Cask> casks) {
        this.casks = casks;
        return this;
    }

    public List<Cask> getCasks() {
        return casks;
    }

    public Cellar addCask(Cask cask) {
        casks.add(cask);
        return this;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public Cellar setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
        return this;
    }
}
