package com.pints793.cask;

public class RemoveCaskRequest extends AbstractCaskRequest {

    private String caskId;

    public  String getCaskId() {
        return caskId;
    }

    public void setCaskName(String caskId) {
        this.caskId = caskId;
    }

}
