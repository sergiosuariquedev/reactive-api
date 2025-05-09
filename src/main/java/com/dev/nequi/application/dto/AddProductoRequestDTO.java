package com.dev.nequi.application.dto;

public record AddProductoRequestDTO(
    String sucursalId,
    String nombre,
    int stock
) {}