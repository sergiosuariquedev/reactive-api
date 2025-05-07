package com.dev.nequi.domain.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private Integer stock;
}
