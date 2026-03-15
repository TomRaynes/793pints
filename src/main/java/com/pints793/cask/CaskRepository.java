package com.pints793.cask;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CaskRepository extends MongoRepository<Cask, String> {
    List<Cask> findByName(String name);
    void removeById(String id);
}
