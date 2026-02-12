package com.pints793.rest.controller;

import com.pints793.cellar.Cellar;
import com.pints793.cellar.CellarRequest;
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
@RequestMapping("/api/v1/cellar")
public class CellarController extends ControllerSupport {

    @PostMapping("/get")
    public ResponseEntity<?> getCellarData(@RequestHeader("Authorization") String token,
                                           @RequestBody CellarRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String cellarName = request.getCellarName();

        List<Cellar> matches = cellarCollection.findByName(cellarName);

        Cellar cellar = matches.getFirst();



        return ResponseEntity.ok(cellar);
    }

    @PostMapping("/new")
    public ResponseEntity<?> newCellar(@RequestHeader("Authorization") String token,
                                       @RequestBody CellarRequest request) {
        User user = getUser(token);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Set<Organisation> organisations = getOrganisations(user);

        String cellarName = request.getCellarName();

        List<Cellar> matches = cellarCollection.findByName(cellarName);

        Cellar cellar = matches.getFirst();



        return ResponseEntity.ok(cellar);
    }
}
