package com.pints793.organisation;

import com.pints793.IdType;
import com.pints793.Utils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Invitations")
public class Invitation {

    @Id
    private String id;
    private String recipientUserId;
    private String senderUserId;
    private String senderUsername;
    private String organisationName;
    private String organisationId;

    public Invitation() {}

    public Invitation(String recipientUserId,
                      String senderUserId,
                      String senderUsername,
                      String organisationName,
                      String organisationId) {

        this.id = Utils.newId(IdType.INVITATION);
        this.recipientUserId = recipientUserId;
        this.senderUserId = senderUserId;
        this.senderUsername = senderUsername;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
    }

    public String getId() {
        return id;
    }

    public Invitation setId(String id) {
        this.id = id;
        return this;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public Invitation setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
        return this;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public Invitation setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
        return this;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public Invitation setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
        return this;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public Invitation setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        return this;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public Invitation setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
        return this;
    }
}
