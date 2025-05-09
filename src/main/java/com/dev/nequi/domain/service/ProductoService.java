package com.dev.nequi.domain.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dev.nequi.application.dto.ProductoMaxStockResponseDTO;
import com.dev.nequi.application.dto.UpdateStockRequestDTO;
import com.dev.nequi.application.dto.ActualizarNombreDTO;
import com.dev.nequi.application.dto.AddProductoRequestDTO;
import com.dev.nequi.domain.model.Producto;
import com.dev.nequi.domain.model.Sucursal;
import com.dev.nequi.infrastructure.repository.ProductoRepository;
import com.dev.nequi.infrastructure.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final SucursalRepository sucursalRepository;

    private final ProductoRepository productoRepository;

    public Mono<Producto> addProducto(AddProductoRequestDTO productoDTO) {
        return sucursalRepository.findById(productoDTO.sucursalId())
            .switchIfEmpty(Mono.error(new RuntimeException("Sucursal no encontrada")))
            .flatMap(sucursal -> {
                Producto producto = Producto.builder()
                    .nombre(productoDTO.nombre())
                    .stock(productoDTO.stock())
                    .sucursalId(productoDTO.sucursalId())
                    .franquiciaId(sucursal.getFranquiciaId())
                    .build();
                return productoRepository.save(producto);
            });
    }


    public Mono<Producto> deleteProducto(String productoId) {
        return productoRepository.findById(productoId)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(producto -> {
                    return productoRepository.delete(producto)
                            .then(Mono.just(producto));
                });
    }

     public Mono<Producto> updateProductoStock(String productoId, UpdateStockRequestDTO dto) {
        return productoRepository.findById(productoId)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(producto -> {
                    producto.setStock(dto.stock());
                    return productoRepository.save(producto);
                });
    }

    public Mono<List<ProductoMaxStockResponseDTO>> obtenerProductoConMasStockPorSucursal2(String franquiciaId) {
        return productoRepository.findByFranquiciaId(franquiciaId)
                .collectList()
                .flatMap(productos -> {
                    List<ProductoMaxStockResponseDTO> response = new ArrayList<>();
                    productos.stream()
                            .collect(Collectors.groupingBy(Producto::getSucursalId))
                            .forEach((sucursalId, productosPorSucursal) -> {
                                Optional<Producto> productoConMaxStock = productosPorSucursal.stream()
                                        .max(Comparator.comparing(Producto::getStock));
                                Sucursal sucursal = sucursalRepository.findById(sucursalId)
                                        .switchIfEmpty(Mono.error(new RuntimeException("Sucursal no encontrada")))
                                        .block();
                                productoConMaxStock.ifPresent(producto -> {
                                    response.add(new ProductoMaxStockResponseDTO(
                                            sucursalId,
                                            sucursal.getNombre(),
                                            producto.getNombre(),
                                            producto.getId(),
                                            producto.getStock()));
                                });
                            });
                    return Mono.just(response);
                });
    }

    public Mono<List<ProductoMaxStockResponseDTO>> obtenerProductoConMasStockPorSucursal(String franquiciaId) {
        return productoRepository.findByFranquiciaId(franquiciaId)
            .collectList()
            .flatMapMany(productos -> {
                Map<String, Optional<Producto>> maxProductosPorSucursal = productos.stream()
                    .collect(Collectors.groupingBy(
                        Producto::getSucursalId,
                        Collectors.maxBy(Comparator.comparing(Producto::getStock))
                    ));

                return Flux.fromIterable(maxProductosPorSucursal.entrySet())
                    .flatMap(entry -> {
                        String sucursalId = entry.getKey();
                        Optional<Producto> optionalProducto = entry.getValue();

                        if (optionalProducto.isEmpty()) return Mono.empty();

                        Producto producto = optionalProducto.get();
                        return sucursalRepository.findById(sucursalId)
                            .switchIfEmpty(Mono.error(new RuntimeException("Sucursal no encontrada")))
                            .map(sucursal -> new ProductoMaxStockResponseDTO(
                                sucursalId,
                                sucursal.getNombre(),
                                producto.getId(),
                                producto.getNombre(),
                                producto.getStock()
                            ));
                    });
            }).collectList();
    }


    public Mono<Producto> updateProductoName(String productoId, ActualizarNombreDTO dto) {
        return productoRepository.findById(productoId)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(producto -> {
                    producto.setNombre(dto.nuevoNombre());
                    return productoRepository.save(producto);
                });
    }

    public Mono<Producto> getProductoById(String id) {
        return productoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")));
    }

    public Mono<List<Producto>> getAllProductos() {
        return productoRepository.findAll()
                .collectList()
                .flatMap(nullList -> {
                    if (nullList.isEmpty()) {
                        return Mono.error(new RuntimeException("No se encontraron productos"));
                    }
                    return Mono.just(nullList);
                });
    }
}
