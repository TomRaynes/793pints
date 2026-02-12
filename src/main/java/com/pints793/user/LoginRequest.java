package com.pints793.user;

public class LoginRequest {
    private String identifier; // username or email
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String username) {
        this.identifier = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
