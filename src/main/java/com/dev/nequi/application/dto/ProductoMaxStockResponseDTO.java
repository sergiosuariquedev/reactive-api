package com.dev.nequi.application.dto;

public record ProductoMaxStockResponseDTO(
    String sucursalId,
    String sucursalNombre,
    String productoId,
    String productoNombre,
    Integer stock
) {

}
