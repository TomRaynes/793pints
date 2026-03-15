package com.pints793.cask;

public class UpdateCaskRequest extends AbstractCaskRequest {

    private String caskId;
    private String caskName;
    private String state;
    private String rackCooldownHours;
    private String ventCooldownHours;
    private String tapCooldownHours;
    private String pullingPeriodHours;

    public String  getCaskId() {
        return caskId;
    }

    public void setCaskId(String caskId) {
        this.caskId = caskId;
    }

    public String getCaskName() {
        return caskName;
    }

    public void setCaskName(String caskName) {
        this.caskName = caskName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRackCooldownHours() {
        return rackCooldownHours;
    }

    public void setRackCooldownHours(String rackCooldownHours) {
        this.rackCooldownHours = rackCooldownHours;
    }

    public String getVentCooldownHours() {
        return ventCooldownHours;
    }

    public void setVentCooldownHours(String ventCooldownHours) {
        this.ventCooldownHours = ventCooldownHours;
    }

    public String getTapCooldownHours() {
        return tapCooldownHours;
    }

    public void setTapCooldownHours(String tapCooldownHours) {
        this.tapCooldownHours = tapCooldownHours;
    }

    public String getPullingPeriodHours() {
        return pullingPeriodHours;
    }

    public void setPullingPeriodHours(String pullingPeriodHours) {
        this.pullingPeriodHours = pullingPeriodHours;
    }
}
