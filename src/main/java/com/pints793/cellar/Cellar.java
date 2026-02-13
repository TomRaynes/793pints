package com.pints793.cellar;

import com.pints793.IdType;
import com.pints793.Utils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Cellar-Management")
public class Cellar {

    @Id
    private String id;
    private String organisationId;
    private String name;
    private Set<String> casks;

    public Cellar() {
        casks = new HashSet<>();
    }

    public Cellar(String name, String organisationId) {
        this.id = Utils.newId(IdType.CELLAR);
        this.organisationId = organisationId;
        this.name = name;
        this.casks = new HashSet<>();
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

    public Cellar setCasks(Set<String> casks) {
        this.casks = casks;
        return this;
    }

    public Set<String> getCasks() {
        return casks;
    }

    public Cellar addCask(String caskId) {
        casks.add(caskId);
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
