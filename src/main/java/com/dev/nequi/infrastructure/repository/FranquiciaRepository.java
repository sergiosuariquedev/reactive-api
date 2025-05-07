package com.dev.nequi.infrastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.dev.nequi.domain.model.Franquicia;

public interface FranquiciaRepository extends ReactiveMongoRepository<Franquicia, String> {
    // Custom query methods can be defined here if needed
    /*
     * 
     * mongodb+srv://srsq02:jlfGEYE2HbC21az7@cluster0.dgha4lq.mongodb.net/
     * contratos?retryWrites=true&w=majority&appName=Cluster0
     * sergiosuarique
     * 3z6repj9e8Vvw84z
     * 
     * mongodb+srv://sergiosuarique:3z6repj9e8Vvw84z@cluster0.aalezgz.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0
     */
    
}
