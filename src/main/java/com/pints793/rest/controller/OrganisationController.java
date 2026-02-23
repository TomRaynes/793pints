package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.organisation.NewOrganisationRequest;
import com.pints793.organisation.Organisation;
import com.pints793.organisation.RenameOrganisationRequest;
import com.pints793.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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
    public ResponseEntity<?> newOrganisation(@RequestHeader(value = "Authorization", required = false) String token,
                                             @RequestBody NewOrganisationRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Organisation organisation = new Organisation(request.getName(), user.getId());
        user.addOrganisation(organisation.getId());

        organisationCollection.save(organisation);
        userCollection.save(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/rename")
    public ResponseEntity<?> renameOrganisation(@RequestHeader(value = "Authorization", required = false) String token,
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

    @GetMapping("get/all")
    public ResponseEntity<?> getAllOrganisations(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(getOrganisations(user));
    }
}
