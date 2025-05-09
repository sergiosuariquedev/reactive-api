package com.dev.nequi.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.nequi.domain.model.Franquicia;

public interface FranquiciaRepository extends ReactiveMongoRepository<Franquicia, String> {
    
}
