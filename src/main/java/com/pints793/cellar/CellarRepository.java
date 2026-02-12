package com.pints793.cellar;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CellarRepository extends MongoRepository<Cellar, String> {

    List<Cellar> findByName(String name);
}
