package com.pints793.dto.cellar;

import java.time.OffsetDateTime;

public class Cask {
    private String name;
    private CaskState state;
    private String imageUrl;
    private OffsetDateTime receivedDate;
    private OffsetDateTime expiryDate;

    public Cask() {
    }

    public String getName() {
        return name;
    }

    public Cask setName(String name) {
        this.name = name;
        return this;
    }

    public CaskState getState() {
        return state;
    }

    public Cask setState(CaskState state) {
        this.state = state;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Cask setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public OffsetDateTime getReceivedDate() {
        return receivedDate;
    }

    public Cask setReceivedDate(OffsetDateTime receivedDate) {
        this.receivedDate = receivedDate;
        return this;
    }

    public OffsetDateTime getExpiryDate() {
        return expiryDate;
    }

    public Cask setExpiryDate(OffsetDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }
}
