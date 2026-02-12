package com.pints793.organisation;

import com.pints793.IdType;
import com.pints793.Utils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "Organisation")
public class Organisation {

    @Id
    private String id;
    private String name;
    private String ownerUserId;
    private Set<String> adminUserIds;
    private Set<String> memberUserIds;
    private Set<String> cellars;

    public Organisation() {}

    public Organisation(String name, String userId) {
        this.id = Utils.newId(IdType.ORGANISATION);
        this.name = name;
        this.ownerUserId = userId;
        this.adminUserIds = Set.of(userId);
        this.memberUserIds = Set.of(userId);
        this.cellars = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public Organisation setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Organisation setName(String name) {
        this.name = name;
        return this;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public Organisation setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public Set<String> getAdminUserIds() {
        return adminUserIds;
    }

    public Organisation setAdminUserIds(Set<String> adminUserIds) {
        this.adminUserIds = adminUserIds;
        return this;
    }

    public Set<String> getMemberUserIds() {
        return memberUserIds;
    }

    public Organisation setMemberUserIds(Set<String> memberUserIds) {
        this.memberUserIds = memberUserIds;
        return this;
    }

    public Organisation addAdmin(String userId) throws OrganisationException {
        if (adminUserIds.contains(userId)) {
            throw new OrganisationException.AddingExistingAdmin();
        }
        memberUserIds.add(userId);
        adminUserIds.add(userId);
        return this;
    }

    public void removeAdmin(String userId) throws OrganisationException {
        if (!adminUserIds.contains(userId)) {
            throw new OrganisationException.RemovingNonAdmin();
        }
        if (ownerUserId.equals(userId)) {
            throw new OrganisationException.RemovingOwnerAsAdmin();
        }
        this.adminUserIds.remove(userId);
    }

    public Organisation addMember(String userId) throws OrganisationException {
        if (memberUserIds.contains(userId)) {
            throw new OrganisationException.AddingExistingMember();
        }
        memberUserIds.add(userId);
        return this;
    }

    public void removeMember(String userId) throws OrganisationException {
        if (!memberUserIds.contains(userId)) {
            throw new OrganisationException.RemovingNonMember();
        }
        if (ownerUserId.equals(userId)) {
            throw new OrganisationException.RemovingOwnerAsMember();
        }
        adminUserIds.remove(userId);
        memberUserIds.remove(userId);
    }

    public Set<String> getCellars() {
        return cellars;
    }

    public Organisation setCellars(Set<String> cellars) {
        this.cellars = cellars;
        return this;
    }

    public Organisation addCellar(String cellarId) {
        cellars.add(cellarId);
        return this;
    }

    public Organisation removeCellar(String cellarId) {
        cellars.remove(cellarId);
        return this;
    }
}
