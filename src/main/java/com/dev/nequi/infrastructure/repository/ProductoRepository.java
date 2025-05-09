package com.dev.nequi.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.nequi.domain.model.Producto;

import reactor.core.publisher.Flux;

public interface ProductoRepository extends ReactiveMongoRepository<Producto, String> {
    /**
     * Find all Productos by Franquicia ID
     *
     * @param franquiciaId the ID of the Franquicia
     * @return a Flux of Producto
     */
    Flux<Producto> findByFranquiciaId(String franquiciaId);
}
