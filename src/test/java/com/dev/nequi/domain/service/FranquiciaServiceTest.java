package com.dev.nequi.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.domain.model.Franquicia;
import com.dev.nequi.infrastructure.repository.FranquiciaRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceTest {

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @InjectMocks
    private FranquiciaService franquiciaService;

    @Test
    void createFranquicia_shouldSaveFranquicia() {
        FranquiciaRequestDTO dto = new FranquiciaRequestDTO("TiendaX");
        Franquicia expected = Franquicia.builder().nombre("TiendaX").build();

        when(franquiciaRepository.save(any())).thenReturn(Mono.just(expected));

        Mono<Franquicia> result = franquiciaService.createFranquicia(dto);

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getNombre().equals("TiendaX"))
                .verifyComplete();

        verify(franquiciaRepository).save(any(Franquicia.class));
    }

    @Test
    void updateFranquiciaName_shouldUpdateNombre() {
        Franquicia franquicia = Franquicia.builder().id("123").nombre("ViejoNombre").build();
        Franquicia updated = Franquicia.builder().id("123").nombre("NuevoNombre").build();

        when(franquiciaRepository.findById("123")).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any())).thenReturn(Mono.just(updated));

        Mono<Franquicia> result = franquiciaService.updateFranquiciaName("123", "NuevoNombre");

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getNombre().equals("NuevoNombre"))
                .verifyComplete();
    }

    @Test
    void updateFranquiciaName_shouldReturnErrorWhenNotFound() {
        when(franquiciaRepository.findById("notFound")).thenReturn(Mono.empty());

        Mono<Franquicia> result = franquiciaService.updateFranquiciaName("notFound", "Nombre");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Franquicia no encontrada"))
                .verify();
    }

    @Test
    void getFranquiciaById_shouldReturnFranquicia() {
        Franquicia franquicia = Franquicia.builder().id("1").nombre("Tienda1").build();
        when(franquiciaRepository.findById("1")).thenReturn(Mono.just(franquicia));

        Mono<Franquicia> result = franquiciaService.getFranquiciaById("1");

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void getFranquiciaById_shouldReturnErrorIfNotFound() {
        when(franquiciaRepository.findById("999")).thenReturn(Mono.empty());

        Mono<Franquicia> result = franquiciaService.getFranquiciaById("999");

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Franquicia no encontrada"))
                .verify();
    }

    @Test
    void getAllFranquicias_shouldReturnList() {
        List<Franquicia> franquicias = List.of(
                Franquicia.builder().id("1").nombre("A").build(),
                Franquicia.builder().id("2").nombre("B").build()
        );

        when(franquiciaRepository.findAll()).thenReturn(Flux.fromIterable(franquicias));

        Mono<List<Franquicia>> result = franquiciaService.getAllFranquicias();

        StepVerifier.create(result)
                .expectNextMatches(list -> list.size() == 2)
                .verifyComplete();
    }

    @Test
    void getAllFranquicias_shouldReturnErrorIfEmpty() {
        when(franquiciaRepository.findAll()).thenReturn(Flux.empty());

        Mono<List<Franquicia>> result = franquiciaService.getAllFranquicias();

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("No se encontraron franquicias"))
                .verify();
    }
}

