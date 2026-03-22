package com.pints793.mongo;

import com.pints793.organisation.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends MongoRepository<Invitation, String> {

    List<Invitation> findByRecipientUserId(String userId);
    void removeById(String id);
}