package com.pints793.mongo;

import com.pints793.cask.Cask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CaskRepository extends MongoRepository<Cask, String> {
    List<Cask> findByName(String name);
    void removeById(String id);
}
