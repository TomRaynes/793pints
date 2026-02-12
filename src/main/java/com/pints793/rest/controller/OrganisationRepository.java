package com.pints793.rest.controller;

import com.pints793.organisation.Organisation;
import com.pints793.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrganisationRepository extends MongoRepository<Organisation, String> {

    List<Organisation> findByName(String name);
    List<Organisation> findByOwnerUserId(String userId);
}