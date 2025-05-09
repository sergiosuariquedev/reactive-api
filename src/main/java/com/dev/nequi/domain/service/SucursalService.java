package com.dev.nequi.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dev.nequi.application.dto.ActualizarNombreDTO;
import com.dev.nequi.application.dto.AddSucursalRequestDTO;
import com.dev.nequi.domain.model.Sucursal;
import com.dev.nequi.infrastructure.repository.SucursalRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SucursalService {
    private final SucursalRepository sucursalRepository;

    public Mono<Sucursal> addSucursal(AddSucursalRequestDTO sucursalDTO) {
        return sucursalRepository.save(Sucursal.builder()
                .nombre(sucursalDTO.nombre())
                .franquiciaId(sucursalDTO.franquiciaId())
                .build());
    }

    public Mono<Sucursal> getSucursalById(String id) {
        return sucursalRepository.findById(id);
    }

    public Mono<List<Sucursal>> getAllSucursales() {
        return sucursalRepository.findAll()
                .collectList();
    }

    public Mono<List<Sucursal>> getSucursalesByFranquiciaId(String franquiciaId) {
        return sucursalRepository.findByFranquiciaId(franquiciaId)
                .collectList();
    }

    public Mono<Sucursal> updateSucursalNameById(String sucursalId, ActualizarNombreDTO dto) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new RuntimeException("Sucursal no encontrada")))
                .flatMap(sucursal -> {
                    sucursal.setNombre(dto.nuevoNombre());
                    return sucursalRepository.save(sucursal);
                });
    }
}
