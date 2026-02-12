package com.pints793.rest.controller;

import com.pints793.Utils;
import com.pints793.cellar.CellarRepository;
import com.pints793.organisation.Organisation;
import com.pints793.user.User;
import com.pints793.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ControllerSupport {

    @Autowired CellarRepository cellarCollection;
    @Autowired UserRepository userCollection;
    @Autowired OrganisationRepository organisationCollection;

    private static final String BEARER = "Bearer ";

    protected User getUser(String token) {
        if (!token.startsWith(BEARER)) {
            return null;
        }
        try {
            String username = Utils.authenticateToken(token.substring(BEARER.length()));
            return userCollection.findByUsername(username).getFirst();
        } catch (RuntimeException e) {
            return null;
        }
    }

    protected Set<Organisation> getOrganisations(User user) {
        Set<String> organisationIds = user.getOrganisationIds();
        Set<Organisation> organisations = new HashSet<>();
        organisationIds.forEach(organisationId -> organisationCollection
                .findById(organisationId).ifPresent(organisations::add));
        return organisations;
    }
}
