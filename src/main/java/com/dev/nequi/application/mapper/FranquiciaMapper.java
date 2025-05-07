package com.dev.nequi.application.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.domain.model.Franquicia;
import com.dev.nequi.domain.model.Producto;
import com.dev.nequi.domain.model.Sucursal;

public class FranquiciaMapper {
    public static Franquicia toEntity(FranquiciaRequestDTO dto) {
        List<Sucursal> sucursales = Optional.ofNullable(dto.sucursales())
            .orElse(Collections.emptyList())
            .stream()
            .map(sucursalDto -> 
            Sucursal.builder()
                .nombre(sucursalDto.nombre())
                .productos(Optional.ofNullable(sucursalDto.productos())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(productoDto -> Producto.builder()
                        .nombre(productoDto.nombre())
                        .stock(productoDto.stock())
                        .build()
                    )
                    .collect(Collectors.toList()))
                .build()
            ).collect(Collectors.toList());

        return Franquicia.builder()
            .nombre(dto.nombre())
            .sucursales(sucursales)
            .build();
    }
}

