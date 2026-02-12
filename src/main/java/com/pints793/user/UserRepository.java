package com.pints793.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

        List<User> findByUsername(String name);
        List<User> findByEmail(String email);
}
