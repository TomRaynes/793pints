package com.pints793.user;

public class UserProfileResponse {

    private String username;
    private String email;
    private String name;
    private String bio;
    private String profilePicture;

    public String getUsername() {
        return username;
    }

    public UserProfileResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserProfileResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserProfileResponse setName(String name) {
        this.name = name;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public UserProfileResponse setBio(String bio) {
        this.bio = bio;
        return this;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public UserProfileResponse setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }
}
