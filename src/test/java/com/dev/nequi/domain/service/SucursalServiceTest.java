package com.dev.nequi.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dev.nequi.application.dto.ActualizarNombreDTO;
import com.dev.nequi.application.dto.AddSucursalRequestDTO;
import com.dev.nequi.domain.model.Sucursal;
import com.dev.nequi.infrastructure.repository.SucursalRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private Sucursal sucursal;
    private AddSucursalRequestDTO addSucursalDTO;
    private ActualizarNombreDTO actualizarNombreDTO;

    @BeforeEach
    void setUp() {
        sucursal = Sucursal.builder()
                .id("s1")
                .nombre("Sucursal A")
                .franquiciaId("f1")
                .build();

        addSucursalDTO = new AddSucursalRequestDTO("Sucursal A", "f1");
        actualizarNombreDTO = new ActualizarNombreDTO("Sucursal Actualizada");
    }

    @Test
    void addSucursal_shouldSaveAndReturnSucursal() {
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(Mono.just(sucursal));

        Mono<Sucursal> result = sucursalService.addSucursal(addSucursalDTO);

        StepVerifier.create(result)
                .expectNextMatches(s -> s.getNombre().equals("Sucursal A") && s.getFranquiciaId().equals("f1"))
                .verifyComplete();
    }

    @Test
    void getSucursalById_shouldReturnSucursalIfExists() {
        when(sucursalRepository.findById("s1")).thenReturn(Mono.just(sucursal));

        Mono<Sucursal> result = sucursalService.getSucursalById("s1");

        StepVerifier.create(result)
                .expectNext(sucursal)
                .verifyComplete();
    }

    @Test
    void getSucursalById_shouldReturnEmptyIfNotExists() {
        when(sucursalRepository.findById("s1")).thenReturn(Mono.empty());

        Mono<Sucursal> result = sucursalService.getSucursalById("s1");

        StepVerifier.create(result)
                .verifyComplete();  // Verifica Mono.empty()
    }

    @Test
    void getAllSucursales_shouldReturnList() {
        when(sucursalRepository.findAll()).thenReturn(Flux.just(sucursal));

        Mono<List<Sucursal>> result = sucursalService.getAllSucursales();

        StepVerifier.create(result)
                .expectNextMatches(list -> list.size() == 1 && list.get(0).equals(sucursal))
                .verifyComplete();
    }

    @Test
    void getSucursalesByFranquiciaId_shouldReturnMatchingList() {
        when(sucursalRepository.findByFranquiciaId("f1")).thenReturn(Flux.just(sucursal));

        Mono<List<Sucursal>> result = sucursalService.getSucursalesByFranquiciaId("f1");

        StepVerifier.create(result)
                .expectNextMatches(list -> list.size() == 1 && list.get(0).getFranquiciaId().equals("f1"))
                .verifyComplete();
    }

    @Test
    void updateSucursalNameById_shouldUpdateIfExists() {
        when(sucursalRepository.findById("s1")).thenReturn(Mono.just(sucursal));
        when(sucursalRepository.save(any(Sucursal.class))).thenReturn(Mono.just(
                Sucursal.builder()
                        .id("s1")
                        .nombre("Sucursal Actualizada")
                        .franquiciaId("f1")
                        .build()
        ));

        Mono<Sucursal> result = sucursalService.updateSucursalNameById("s1", actualizarNombreDTO);

        StepVerifier.create(result)
                .expectNextMatches(s -> s.getNombre().equals("Sucursal Actualizada"))
                .verifyComplete();
    }

    @Test
    void updateSucursalNameById_shouldReturnErrorIfNotFound() {
        when(sucursalRepository.findById("s1")).thenReturn(Mono.empty());

        Mono<Sucursal> result = sucursalService.updateSucursalNameById("s1", actualizarNombreDTO);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }
}

