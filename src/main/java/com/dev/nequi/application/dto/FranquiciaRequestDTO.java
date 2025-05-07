package com.dev.nequi.application.dto;

import java.util.List;

public record FranquiciaRequestDTO(
    String nombre,
    List<SucursalRequestDTO> sucursales
) {
    
}