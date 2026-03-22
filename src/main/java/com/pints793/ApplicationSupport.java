package com.pints793;

import com.pints793.mongo.CaskRepository;
import com.pints793.cellar.Cellar;
import com.pints793.mongo.CellarRepository;
import com.pints793.mongo.InvitationRepository;
import com.pints793.organisation.Organisation;
import com.pints793.mongo.OrganisationRepository;
import com.pints793.organisation.OrganisationException;
import com.pints793.user.User;
import com.pints793.mongo.UserRepository;
import com.pints793.user.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired protected InvitationRepository invitationCollection;

    protected static final String USERNAME_REGEX = "^[A-Za-z0-9_\\- ]{3,32}$";
    protected static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    protected static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

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
    }

    protected boolean addUserToOrganisation(User user, Organisation organisation) {
        try {
            organisation.addMember(user.getId());
            user.addOrganisation(organisation.getId());
        } catch (OrganisationException | UserException e) {
            return false;
        }
        organisationCollection.save(organisation);
        userCollection.save(user);
        return true;
    }
}
