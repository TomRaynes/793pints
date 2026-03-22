package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.EntityLabel;
import com.pints793.organisation.AcceptInviteToOrganisationRequest;
import com.pints793.organisation.GetAccessLevelRequest;
import com.pints793.organisation.GetAccessLevelResponse;
import com.pints793.organisation.Invitation;
import com.pints793.organisation.InviteToOrganisationRequest;
import com.pints793.organisation.NewOrganisationRequest;
import com.pints793.organisation.Organisation;
import com.pints793.organisation.OrganisationException;
import com.pints793.organisation.RenameOrganisationRequest;
import com.pints793.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organisation")
public class OrganisationController extends ApplicationSupport {

    @PostMapping("/new")
    public ResponseEntity<?> newOrganisation(@RequestHeader("Authorization") String token,
                                             @RequestBody NewOrganisationRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Organisation organisation = new Organisation(request.getName(), user.getId());
        user.addOrganisation(organisation.getId());

        organisationCollection.save(organisation);
        userCollection.save(user);

        return ResponseEntity.ok(new EntityLabel(organisation.getId(), organisation.getName()));
    }

    @PostMapping("/rename")
    public ResponseEntity<?> renameOrganisation(@RequestHeader("Authorization") String token,
                                                @RequestBody RenameOrganisationRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Organisation> matches = organisationCollection.findByOwnerUserId(user.getId());

        if (matches.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String oldName = request.getOldName();
        String newName = request.getNewName();
        Organisation organisation = null;

        for (Organisation org : matches) {
            if (org.getName().equals(oldName)) {
                organisation = org;
            }
        }
        if (organisation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        organisation.setName(newName);
        organisationCollection.save(organisation);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllOrganisations(@RequestHeader("Authorization") String token) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(getOrganisations(user));
    }

    @PostMapping("/invite")
    public ResponseEntity<?> inviteToOrganisation(@RequestHeader("Authorization") String token,
                                                  @RequestBody InviteToOrganisationRequest request) {
        User sender = getUser(token);

        if (sender == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String organisationId = request.getOrganisationId();

        if (!sender.isInOrganisation(organisationId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Organisation organisation = getOrganisation(organisationId);

        if (!organisation.getOwnerUserId().equals(sender.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<User> matchedRecipients;
        String recipientIdentifier = request.getRecipientIdentifier();

        if (recipientIdentifier.matches(USERNAME_REGEX)) {
            matchedRecipients = userCollection.findByUsername(recipientIdentifier);
        } else if (recipientIdentifier.matches(EMAIL_REGEX)) {
            matchedRecipients = userCollection.findByEmail(recipientIdentifier);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (matchedRecipients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (matchedRecipients.size() > 1) {
            // TODO: log
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        User recipient = matchedRecipients.get(0);

        invitationCollection.save(new Invitation(
                recipient.getId(),
                sender.getId(),
                sender.getUsername(),
                organisation.getName(),
                organisation.getId()
        ));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<?> acceptInviteToOrganisation(@RequestHeader("Authorization") String token,
                                                        @RequestBody AcceptInviteToOrganisationRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Invitation invitation = invitationCollection.findById(request.getInvitationId()).orElse(null);

        if (invitation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Organisation organisation = getOrganisation(invitation.getOrganisationId());

        if (!addUserToOrganisation(user, organisation)) {
            // TODO: log already in organisation
        }
        invitationCollection.removeById(invitation.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/user/access_level")
    public ResponseEntity<?> getAccessLevel(@RequestHeader("Authorization") String token,
                                            @RequestBody GetAccessLevelRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String organisationId = request.getOrganisationId();

        if (!user.isInOrganisation(organisationId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Organisation organisation = getOrganisation(organisationId);

        if (organisation.getOwnerUserId().equals(user.getId())) {
            return ResponseEntity.ok(new GetAccessLevelResponse("Owner"));
        } else if (organisation.getAdminUserIds().contains(user.getId())) {
            return ResponseEntity.ok(new GetAccessLevelResponse("Admin"));
        }
        return ResponseEntity.ok(new GetAccessLevelResponse("Member"));
    }
}
