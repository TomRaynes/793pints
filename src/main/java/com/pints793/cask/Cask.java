package com.pints793.cask;

import com.pints793.DefaultConfig;
import com.pints793.IdType;
import com.pints793.Utils;
import com.pints793.cellar.CellarConfig;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;

@Document(collection = "Cask")
public class Cask {

    @Id
    private String id;
    private String name;
    private String cellarId;
    private CaskState state;
    private String stateChangeTimestamp;
    private String imageUrl;
    private String receivedDate;
    private String expiryDate;
    private long rackCooldownHours;
    private long ventCooldownHours;
    private long tapCooldownHours;
    private long pullingPeriodHours;

    public Cask() {}

    public Cask(String name, String cellarId, CaskState state, CellarConfig config) {
        this.id = Utils.newId(IdType.CASK);
        this.name = name;
        this.cellarId = cellarId;
        this.state = state != null ? state : CaskState.DELIVERED;
        this.stateChangeTimestamp = now().toString();
        this.rackCooldownHours = config.getRackCooldownDefault();
        this.ventCooldownHours = config.getVentCooldownDefault();
        this.tapCooldownHours = config.getTapCooldownDefault();
        this.pullingPeriodHours = config.getPullingPeriodDefault();
    }

    public String getId() {
        return id;
    }

    public Cask setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Cask setName(String name) {
        this.name = name;
        return this;
    }

    public String getCellarId() {
        return cellarId;
    }

    public Cask setCellarId(String cellarId) {
        this.cellarId = cellarId;
        return this;
    }

    public CaskState getState() {
        return state;
    }

    public Cask setState(CaskState state) {
        this.state = state;
        this.stateChangeTimestamp = now().toString();
        return this;
    }

    public String getStateChangeTimestamp() {
        return stateChangeTimestamp;
    }

    public OffsetDateTime getStateChangeTime() {
        return OffsetDateTime.parse(stateChangeTimestamp);
    }

    public Cask setStateChangeTimestamp(String stateChangeTimestamp) {
        this.stateChangeTimestamp = stateChangeTimestamp;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Cask setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public Cask setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
        return this;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public Cask setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public long getRackCooldownHours() {
        return rackCooldownHours;
    }

    public Cask setRackCooldownHours(long rackCooldownHours) {
        this.rackCooldownHours = rackCooldownHours;
        return this;
    }

    public long getVentCooldownHours() {
        return ventCooldownHours;
    }

    public Cask setVentCooldownHours(long ventCooldownHours) {
        this.ventCooldownHours = ventCooldownHours;
        return this;
    }

    public long getTapCooldownHours() {
        return tapCooldownHours;
    }

    public Cask setTapCooldownHours(long tapCooldownHours) {
        this.tapCooldownHours = tapCooldownHours;
        return this;
    }

    public long getPullingPeriodHours() {
        return pullingPeriodHours;
    }

    public Cask setPullingPeriodHours(long pullingPeriodHours) {
        this.pullingPeriodHours = pullingPeriodHours;
        return this;
    }

    public Long getActiveCooldown() {
        return switch (state) {
            case RACKED -> rackCooldownHours;
            case VENTED -> ventCooldownHours;
            case TAPPED -> tapCooldownHours;
            case PULLING -> pullingPeriodHours;
            default -> null;
        };
    }

    public Cask progressState() {
        state = state.getNextState();
        stateChangeTimestamp = now().toString();
        return this;
    }

    public void updateState() {
        long hoursSinceStateChange = Duration.between(getStateChangeTime(), now()).toHours();

    }
}
