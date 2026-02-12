package com.pints793.cellar;

public class Cask {
    private String name;
    private CaskState state;
    private String imageUrl;
    private String receivedDate;
    private String expiryDate;

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
}
