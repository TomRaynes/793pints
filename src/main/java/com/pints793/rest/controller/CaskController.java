package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.cask.AbstractCaskRequest;
import com.pints793.cask.Cask;
import com.pints793.cask.CaskState;
import com.pints793.cask.GetAllCasksRequest;
import com.pints793.cask.GetCaskRequest;
import com.pints793.cask.RemoveCaskRequest;
import com.pints793.cask.UpdateCaskRequest;
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

import java.time.OffsetDateTime;
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
                    CaskState.parse(request.getState()),
                    cellar.getConfig());

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

    @PostMapping("/remove")
    public ResponseEntity<?> removeCask(@RequestHeader("Authorization") String token,
                                        @RequestBody RemoveCaskRequest request) {
        try {
            Cellar cellar = authenticateAndGetCellar(token, request);
            String caskId = request.getCaskId();

            if (!cellar.getCasks().contains(caskId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            cellar.removeCask(caskId);
            caskCollection.removeById(caskId);
            cellarCollection.save(cellar);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ErrorResponseException e) {
            return new ResponseEntity<>(e.getStatus());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCask(@RequestHeader("Authorization") String token,
                                        @RequestBody UpdateCaskRequest request) {
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

            String caskName = request.getCaskName();
            String state = request.getState();
            String rackCooldownHours = request.getRackCooldownHours();
            String ventCooldownHours = request.getVentCooldownHours();
            String tapCooldownHours = request.getTapCooldownHours();
            String pullingPeriodHours = request.getPullingPeriodHours();

            if (caskName != null) {
                cask.setName(caskName);
            }
            if (state != null) {
                CaskState oldState = cask.getState();
                CaskState newState = CaskState.parse(state);

                if (oldState != newState) {
                    cask.setState(newState);
                    cask.setStateChangeTimestamp(OffsetDateTime.now().toString());
                }
            }
            if (rackCooldownHours != null) {
                cask.setRackCooldownHours(Long.parseLong(rackCooldownHours));
            }
            if (ventCooldownHours != null) {
                cask.setVentCooldownHours(Long.parseLong(ventCooldownHours));
            }
            if (tapCooldownHours != null) {
                cask.setTapCooldownHours(Long.parseLong(tapCooldownHours));
            }
            if (pullingPeriodHours != null) {
                cask.setPullingPeriodHours(Long.parseLong(pullingPeriodHours));
            }
            caskCollection.save(cask);
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

