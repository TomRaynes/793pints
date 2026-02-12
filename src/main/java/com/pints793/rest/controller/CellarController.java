package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.cellar.Cellar;
import com.pints793.cellar.GetCellarRequest;
import com.pints793.cellar.NewCellarRequest;
import com.pints793.organisation.Organisation;
import com.pints793.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cellar")
public class CellarController extends ApplicationSupport {

    @PostMapping("/get")
    public ResponseEntity<?> getCellar(@RequestHeader("Authorization") String token,
                                       @RequestBody GetCellarRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String organisationId = request.getOrganisationId();

        if (!user.isInOrganisation(organisationId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Organisation organisation = getOrganisation(organisationId);
        String cellarId = request.getCellarId();

        if (!organisation.getCellars().contains(cellarId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(getCellar(cellarId));
    }

    @PostMapping("/new")
    public ResponseEntity<?> newCellar(@RequestHeader("Authorization") String token,
                                       @RequestBody NewCellarRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String organisationId = request.getOrganisationId();

        if (!user.isInOrganisation(organisationId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Organisation organisation = getOrganisation(organisationId);
        String cellarName = request.getCellarName();

        for (Cellar cellar : getCellars(organisation)) {
            if (cellar.getName().equals(cellarName)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        Cellar cellar = new Cellar(cellarName, organisationId);
        organisation.addCellar(cellar.getId());

        cellarCollection.save(cellar);
        organisationCollection.save(organisation);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
