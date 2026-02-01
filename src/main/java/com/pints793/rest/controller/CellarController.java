package com.pints793.rest.controller;

import com.pints793.dto.cellar.Cask;
import com.pints793.dto.cellar.CaskState;
import com.pints793.dto.cellar.Cellar;
import com.pints793.rest.request.CellarRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/cellar")
public class CellarController {

    @PostMapping("/get")
    public ResponseEntity<Cellar> getCellarData(@RequestBody CellarRequest request) {
        String cellarName = request.getCellarName();

        Cask cask1 = new Cask();
        cask1.setName("cask1")
                .setImageUrl("logos/cask1.png")
                .setState(CaskState.RACKED)
                .setReceivedDate(OffsetDateTime.now())
                .setExpiryDate(OffsetDateTime.now().plusDays(30));

        Cask cask2 = new Cask();
        cask2.setName("cask2")
                .setImageUrl("logos/cask2.png")
                .setState(CaskState.PULLING)
                .setReceivedDate(OffsetDateTime.now())
                .setExpiryDate(OffsetDateTime.now().plusDays(30));

        Cellar cellar = new Cellar();
        cellar.setName(cellarName)
                .addCask(cask1)
                .addCask(cask2);

        return ResponseEntity.ok(cellar);
    }
}
