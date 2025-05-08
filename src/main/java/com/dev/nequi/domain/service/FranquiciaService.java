package com.dev.nequi.domain.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.application.dto.ProductoMaxStockResponseDTO;
import com.dev.nequi.application.dto.ProductoRequestDTO;
import com.dev.nequi.application.dto.SucursalRequestDTO;
import com.dev.nequi.application.mapper.FranquiciaMapper;
import com.dev.nequi.domain.model.Franquicia;
import com.dev.nequi.domain.model.Producto;
import com.dev.nequi.domain.model.Sucursal;
import com.dev.nequi.infrastructure.repository.FranquiciaRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranquiciaService {
    private final FranquiciaRepository franquiciaRepository;
    
    public Mono<Franquicia> crearFranquicia(FranquiciaRequestDTO dto) {
        Franquicia franquicia = FranquiciaMapper.toEntity(dto);
        return franquiciaRepository.save(franquicia);
    }

    public Mono<Franquicia> agregarSucursal(String franquiciaId, SucursalRequestDTO sucursalDTO) {
        return franquiciaRepository.findById(franquiciaId)
        .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
        .flatMap(franquicia -> {
            if (franquicia.getSucursales() == null) {
                franquicia.setSucursales(new ArrayList<>());
            }
            franquicia.getSucursales().add(Sucursal.builder().nombre(sucursalDTO.nombre()).productos(new ArrayList<>()).build());
            return franquiciaRepository.save(franquicia);
        });
    }

    public Mono<Franquicia> agregarProducto(String franquiciaId, String sucursalId, ProductoRequestDTO productoDTO) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .flatMap(franquicia -> {
                Optional<Sucursal> sucursalOpt = franquicia.getSucursales().stream()
                    .filter(s -> s.getId().equals(sucursalId))
                    .findFirst();

                if (sucursalOpt.isEmpty()) {
                    return Mono.error(new RuntimeException("Sucursal no encontrada"));
                }

                Sucursal sucursal = sucursalOpt.get();
                if (sucursal.getProductos() == null) {
                    sucursal.setProductos(new ArrayList<>());
                }
                sucursal.getProductos().add(
                    Producto.builder()
                        .nombre(productoDTO.nombre())
                        .stock(productoDTO.stock())
                        .build()
                );

                return franquiciaRepository.save(franquicia);
            });
    }
    
    public Mono<Franquicia> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .flatMap(franquicia -> {
                Optional<Sucursal> sucursalOpt = franquicia.getSucursales().stream()
                    .filter(s -> s.getId().equals(sucursalId))
                    .findFirst();
    
                if (sucursalOpt.isEmpty()) {
                    return Mono.error(new RuntimeException("Sucursal no encontrada"));
                }
    
                Sucursal sucursal = sucursalOpt.get();
                boolean removed = sucursal.getProductos().removeIf(p -> p.getId().equals(productoId));
    
                if (!removed) {
                    return Mono.error(new RuntimeException("Producto no encontrado en la sucursal"));
                }
    
                return franquiciaRepository.save(franquicia);
            });
    }

    public Mono<Franquicia> actualizarStockProducto(String franquiciaId, String sucursalId, String productoId, Integer nuevoStock) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .flatMap(franquicia -> {
                Optional<Sucursal> sucursalOpt = franquicia.getSucursales().stream()
                    .filter(s -> s.getId().equals(sucursalId))
                    .findFirst();
    
                if (sucursalOpt.isEmpty()) {
                    return Mono.error(new RuntimeException("Sucursal no encontrada"));
                }
    
                Sucursal sucursal = sucursalOpt.get();
                Optional<Producto> productoOpt = sucursal.getProductos().stream()
                    .filter(p -> p.getId().equals(productoId))
                    .findFirst();
    
                if (productoOpt.isEmpty()) {
                    return Mono.error(new RuntimeException("Producto no encontrado"));
                }
    
                productoOpt.get().setStock(nuevoStock);
    
                return franquiciaRepository.save(franquicia);
            });
    }

    public Mono<List<ProductoMaxStockResponseDTO>> obtenerProductoConMasStockPorSucursal(String franquiciaId) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .map(franquicia -> franquicia.getSucursales().stream()
                .map(sucursal -> {
                    return sucursal.getProductos().stream()
                        .max(Comparator.comparing(Producto::getStock))
                        .map(producto -> new ProductoMaxStockResponseDTO(
                            sucursal.getId(),
                            sucursal.getNombre(),
                            producto.getId(),
                            producto.getNombre(),
                            producto.getStock()
                        ))
                        .orElse(null); // Si no hay productos, puede devolver null o ser filtrado
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
            );
    }

    public Mono<Franquicia> actualizarNombreFranquicia(String franquiciaId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .flatMap(franquicia -> {
                franquicia.setNombre(nuevoNombre);
                return franquiciaRepository.save(franquicia);
            });
    }
    
    public Mono<Franquicia> actualizarNombreSucursal(String franquiciaId, String sucursalId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .flatMap(franquicia -> {
                franquicia.getSucursales().stream()
                    .filter(s -> s.getId().equals(sucursalId))
                    .findFirst()
                    .ifPresentOrElse(
                        sucursal -> sucursal.setNombre(nuevoNombre),
                        () -> { throw new RuntimeException("Sucursal no encontrada"); }
                    );
                return franquiciaRepository.save(franquicia);
            });
    }
    
    public Mono<Franquicia> actualizarNombreProducto(String franquiciaId, String sucursalId, String productoId, String nuevoNombre) {
        return franquiciaRepository.findById(franquiciaId)
            .switchIfEmpty(Mono.error(new RuntimeException("Franquicia no encontrada")))
            .flatMap(franquicia -> {
                franquicia.getSucursales().stream()
                    .filter(s -> s.getId().equals(sucursalId))
                    .findFirst()
                    .ifPresentOrElse(
                        sucursal -> {
                            sucursal.getProductos().stream()
                                .filter(p -> p.getId().equals(productoId))
                                .findFirst()
                                .ifPresentOrElse(
                                    producto -> producto.setNombre(nuevoNombre),
                                    () -> { throw new RuntimeException("Producto no encontrado"); }
                                );
                        },
                        () -> { throw new RuntimeException("Sucursal no encontrada"); }
                    );
                return franquiciaRepository.save(franquicia);
            });
    }
    
    public Mono<List<Franquicia>> obtenerTodasLasFranquicias() {
        return franquiciaRepository.findAll()
            .collectList()
            .switchIfEmpty(Mono.error(new RuntimeException("No se encontraron franquicias")));
    
    }
}
