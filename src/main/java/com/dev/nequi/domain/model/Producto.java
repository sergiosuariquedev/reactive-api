package com.dev.nequi.domain.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto {
    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private Integer stock;
    private String sucursalId;
    private String franquiciaId;
}
