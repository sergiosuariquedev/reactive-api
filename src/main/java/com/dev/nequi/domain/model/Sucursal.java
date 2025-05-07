package com.dev.nequi.domain.model;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sucursal {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private List<Producto> productos;
}
