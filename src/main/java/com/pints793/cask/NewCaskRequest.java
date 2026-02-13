package com.pints793.cask;

public class NewCaskRequest extends AbstractCaskRequest {

    private String caskName;
    private String state;

    public  String getCaskName() {
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
}
