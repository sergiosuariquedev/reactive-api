package com.dev.nequi.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.domain.model.Franquicia;
import com.dev.nequi.infrastructure.repository.FranquiciaRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranquiciaService {
    private final FranquiciaRepository franquiciaRepository;

    public Mono<Franquicia> createFranquicia(FranquiciaRequestDTO dto) {
        System.out.println("Creando franquicia: " + dto.nombre());
        Franquicia franquicia = Franquicia.builder()
                .nombre(dto.nombre())
                .build();   
        return franquiciaRepository.save(franquicia);
    }


    public Mono<Franquicia> updateFranquiciaName(String franquiciaId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
                .flatMap(franquicia -> {
                    franquicia.setNombre(nuevoNombre);
                    return franquiciaRepository.save(franquicia);
                });
    }
    public Mono<Franquicia> getFranquiciaById(String franquiciaId) {
        return franquiciaRepository.findById(franquiciaId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")));
    }
    public Mono<List<Franquicia>> getAllFranquicias() {
        return franquiciaRepository.findAll()
            .collectList()
            .flatMap(list -> {
                if (list.isEmpty()) {
                    return Mono.error(new RuntimeException("No se encontraron franquicias"));
                }
                return Mono.just(list);
            });
    }

}
