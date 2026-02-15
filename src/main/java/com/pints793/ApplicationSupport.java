package com.pints793;

import com.pints793.cask.CaskRepository;
import com.pints793.cellar.Cellar;
import com.pints793.cellar.CellarRepository;
import com.pints793.organisation.Organisation;
import com.pints793.organisation.OrganisationRepository;
import com.pints793.user.User;
import com.pints793.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public abstract class ApplicationSupport {

    @Autowired protected CellarRepository cellarCollection;
    @Autowired protected CaskRepository caskCollection;
    @Autowired protected UserRepository userCollection;
    @Autowired protected OrganisationRepository organisationCollection;

    private static final String BEARER = "Bearer ";

    protected User getUser(String token) {
        if (!token.startsWith(BEARER)) {
            return null;
        }
        try {
            String userId = Utils.authenticateToken(token.substring(BEARER.length()));
            Optional<User> user = userCollection.findById(userId);
            return user.orElse(null);
        } catch (RuntimeException e) {
            return null;
        }
    }

    protected Organisation getOrganisation(String organisationId) {
        return organisationCollection.findById(organisationId).orElse(null);
    }

    protected Cellar getCellar(String cellarId) {
        return cellarCollection.findById(cellarId).orElse(null);
    }

    protected Set<EntityLabel> getCellars(Organisation organisation) {
        Set<String> cellarIds = organisation.getCellars();
        Set<EntityLabel> cellars = new HashSet<>();
        cellarIds.forEach(cellarId -> cellarCollection
                .findById(cellarId).ifPresent(cellar ->
                        cellars.add(new EntityLabel().setId(cellarId)
                                                     .setName(cellar.getName())
                        )));
        return cellars;
    }

    protected Set<EntityLabel> getOrganisations(User user) {
        Set<String> organisationIds = user.getOrganisationIds();
        Set<EntityLabel> organisations = new HashSet<>();
        organisationIds.forEach(organisationId -> organisationCollection
                .findById(organisationId).ifPresent(org ->
                        organisations.add(new EntityLabel().setId(organisationId)
                                                                 .setName(org.getName())
                        )));
        return organisations;
    }}
