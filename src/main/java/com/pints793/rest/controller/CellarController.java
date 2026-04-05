package com.pints793.rest.controller;

import com.pints793.ApplicationSupport;
import com.pints793.EntityLabel;
import com.pints793.cellar.Cellar;
import com.pints793.cellar.CellarConfig;
import com.pints793.cellar.GetAllCellarsRequest;
import com.pints793.cellar.GetCellarRequest;
import com.pints793.cellar.NewCellarRequest;
import com.pints793.cellar.UpdateCellarConfigRequest;
import com.pints793.organisation.Organisation;
import com.pints793.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.pints793.cellar.UpdateCellarConfigRequest.Field;

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

        for (EntityLabel cellar : getCellars(organisation)) {
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

    @PostMapping("get/all")
    public ResponseEntity<?> getAllCellars(@RequestHeader("Authorization") String token,
                                           @RequestBody GetAllCellarsRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String organisationId = request.getOrganisationId();

        if (!user.isInOrganisation(organisationId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Organisation organisation = getOrganisation(organisationId);

        return ResponseEntity.ok(getCellars(organisation));
    }

    @GetMapping("/{cellarId}/config")
    public ResponseEntity<?> getCellarConfig(@RequestHeader("Authorization") String token,
                                             @PathVariable("cellarId") String cellarId) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Cellar cellar = getCellar(cellarId);

        if (cellar == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!user.isInOrganisation(cellar.getOrganisationId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(cellar.getConfig());
    }

    @PostMapping("/{cellarId}/update_config")
    public ResponseEntity<?> updateCellarConfig(@RequestHeader("Authorization") String token,
                                                @PathVariable("cellarId") String cellarId,
                                                @RequestBody UpdateCellarConfigRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Cellar cellar = getCellar(cellarId);

        if (cellar == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!user.isInOrganisation(cellar.getOrganisationId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Map<Field, BiConsumer<CellarConfig, Long>> fieldSetters = Map.of(
                request.getRackCooldownDefault(), CellarConfig::setRackCooldownDefault,
                request.getVentCooldownDefault(), CellarConfig::setVentCooldownDefault,
                request.getTapCooldownDefault(), CellarConfig::setTapCooldownDefault,
                request.getPullingPeriodDefault(), CellarConfig::setPullingPeriodDefault
        );

        Set<Map.Entry<Field, BiConsumer<CellarConfig, Long>>> organisationUpdates = new HashSet<>();

        for (Map.Entry<Field, BiConsumer<CellarConfig, Long>> entry : fieldSetters.entrySet()) {
            Field field = entry.getKey();

            if (field.isApplyToAll()) {
                organisationUpdates.add(entry);
            } else {
                entry.getValue().accept(cellar.getConfig(), field.getValue());
            }
        }

        if (!organisationUpdates.isEmpty()) {
            getOrganisation(cellar.getOrganisationId()).getCellars().stream()
                    .map(this::getCellar)
                    .forEach(c -> {
                        for (Map.Entry<Field, BiConsumer<CellarConfig, Long>> entry : organisationUpdates) {
                            entry.getValue().accept(c.getConfig(), entry.getKey().getValue());
                            cellarCollection.save(c);
                        }
                    });
        }
        cellarCollection.save(cellar);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
