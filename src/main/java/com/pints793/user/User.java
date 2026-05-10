package com.pints793.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "User")
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private Set<String> organisationIds;
    private List<String> pinnedCellarIds;

    // Profile
    private String name;
    private String profilePicture;
    private String bio;


    public User() {
        organisationIds = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public Set<String> getOrganisationIds() {
        return organisationIds;
    }

    public User setOrganisationIds(Set<String> organisationIds) {
        this.organisationIds = organisationIds;
        return this;
    }

    public User addOrganisation(String organisationId) throws UserException {
        if (organisationIds.contains(organisationId)) {
            throw new UserException.AlreadyInOrganisation();
        }
        organisationIds.add(organisationId);
        return this;
    }

    public User removeOrganisation(String organisationId) throws UserException {
        if  (!organisationIds.contains(organisationId)) {
            throw new UserException.NotInOrganisation();
        }
        organisationIds.remove(organisationId);
        return this;
    }

    public boolean isInOrganisation(String organisationId) {
        return organisationIds.contains(organisationId);
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public User setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public User setBio(String bio) {
        this.bio = bio;
        return this;
    }

    public List<String> getPinnedCellarIds() {
        return pinnedCellarIds;
    }

    public User setPinnedCellarIds(List<String> pinnedCellarIds) {
        this.pinnedCellarIds = pinnedCellarIds;
        return this;
    }

    public User pinCellar(String cellarId) {
        if (!pinnedCellarIds.contains(cellarId)) {
            pinnedCellarIds.add(cellarId);
        }
        return this;
    }

    public User unpinCellar(String cellarId) throws UserException.CellarNotPinned {
        if (!pinnedCellarIds.contains(cellarId)) {
            throw new UserException.CellarNotPinned();
        }
        pinnedCellarIds.remove(cellarId);
        return this;
    }
}
