package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.cask.AbstractCaskRequest;
import com.pints793.cask.Cask;
import com.pints793.cask.CaskState;
import com.pints793.cask.GetAllCasksRequest;
import com.pints793.cask.GetCaskRequest;
import com.pints793.cellar.Cellar;
import com.pints793.cask.NewCaskRequest;
import com.pints793.organisation.Organisation;
import com.pints793.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/cask")
public class CaskController extends ApplicationSupport {

    @PostMapping("/new")
    public ResponseEntity<?> newCask(@RequestHeader("Authorization") String token,
                                     @RequestBody NewCaskRequest request) {
        try {
            Cellar cellar = authenticateAndGetCellar(token, request);
            Cask cask = new Cask(
                    request.getCaskName(),
                    request.getCellarId(),
                    CaskState.parse(request.getState()));

            cellar.addCask(cask.getId());

            cellarCollection.save(cellar);
            caskCollection.save(cask);

            return ResponseEntity.ok().body(cask);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getStatus());
        }
    }

    @PostMapping("/get")
    public ResponseEntity<?> getCask(@RequestHeader("Authorization") String token,
                                     @RequestBody GetCaskRequest request) {
        try {
            Cellar cellar = authenticateAndGetCellar(token, request);
            String caskId = request.getCaskId();

            if (!cellar.getCasks().contains(caskId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Cask cask = caskCollection.findById(caskId).orElse(null);

            if (cask == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity.ok().body(cask);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getStatus());
        }
    }

    @PostMapping("/get/all")
    public ResponseEntity<?> getAllCasks(@RequestHeader("Authorization") String token,
                                         @RequestBody GetAllCasksRequest request) {
        try {
            Set<String> caskIds = authenticateAndGetCellar(token, request).getCasks();
            List<Cask> casks = caskCollection.findAllById(caskIds);
            return ResponseEntity.ok().body(casks);
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getStatus());
        }
    }

    private <T extends AbstractCaskRequest> Cellar authenticateAndGetCellar(String token, T request)
            throws ErrorResponseException {

        User user = getUser(token);

        if (user == null) {
            throw new ErrorResponseException(HttpStatus.UNAUTHORIZED);
        }
        String organisationId = request.getOrganisationId();

        if (!user.isInOrganisation(organisationId)) {
            throw new ErrorResponseException(HttpStatus.FORBIDDEN);
        }
        Organisation organisation = getOrganisation(organisationId);
        String cellarId = request.getCellarId();

        if (!organisation.getCellars().contains(cellarId)) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND);
        }
        return getCellar(cellarId);
    }
}

