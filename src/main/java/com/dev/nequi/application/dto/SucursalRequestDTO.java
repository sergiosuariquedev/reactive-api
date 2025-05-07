package com.dev.nequi.application.dto;

import java.util.List;

public record SucursalRequestDTO( 
    String nombre,
    List<ProductoRequestDTO> productos) {
}
