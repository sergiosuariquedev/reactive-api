package com.dev.nequi.application.dto;

public record UpdateStockRequestDTO(Integer stock) {
    public UpdateStockRequestDTO {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser nulo o negativo.");
        }
    }
}