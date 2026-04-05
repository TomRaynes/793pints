package com.pints793.cellar;

import com.pints793.DefaultConfig;
import com.pints793.IdType;
import com.pints793.Utils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Cellar-Management")
public class Cellar implements DefaultConfig {

    @Id
    private String id;
    private String organisationId;
    private String name;
    private Set<String> casks;
    private CellarConfig config;

    public Cellar() {
        casks = new HashSet<>();
        config = new CellarConfig();
    }

    public Cellar(String name, String organisationId) {
        this.id = Utils.newId(IdType.CELLAR);
        this.organisationId = organisationId;
        this.name = name;
        this.casks = new HashSet<>();
        this.config = new CellarConfig()
                .setRackCooldownDefault(RACK_COOLDOWN_HOURS_DEFAULT)
                .setTapCooldownDefault(TAP_COOLDOWN_HOURS_DEFAULT)
                .setVentCooldownDefault(VENT_COOLDOWN_HOURS_DEFAULT)
                .setPullingPeriodDefault(PULLING_PERIOD_HOURS_DEFAULT);
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

    public Cellar removeCask(String caskId) {
        casks.remove(caskId);
        return this;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public Cellar setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
        return this;
    }

    public CellarConfig getConfig() {
        return config;
    }

    public Cellar setConfig(CellarConfig config) {
        this.config = config;
        return this;
    }
}
