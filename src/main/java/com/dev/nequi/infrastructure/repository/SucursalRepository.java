package com.dev.nequi.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.nequi.domain.model.Sucursal;

import reactor.core.publisher.Flux;

public interface SucursalRepository extends ReactiveMongoRepository<Sucursal, String> {
    /**
     * Find all Sucursales by Franquicia ID
     *
     * @param franquiciaId the ID of the Franquicia
     * @return a Flux of Sucursal
     */
    Flux<Sucursal> findByFranquiciaId(String franquiciaId);

}
