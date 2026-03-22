package com.pints793.organisation;

public class GetAccessLevelResponse {
    private String accessLevel;

    public GetAccessLevelResponse() {}

    public GetAccessLevelResponse(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}
