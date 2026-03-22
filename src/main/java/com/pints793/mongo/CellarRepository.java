package com.pints793.mongo;

import com.pints793.cellar.Cellar;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CellarRepository extends MongoRepository<Cellar, String> {

    List<Cellar> findByName(String name);
}
