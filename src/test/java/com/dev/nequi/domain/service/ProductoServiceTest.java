package com.dev.nequi.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dev.nequi.application.dto.ActualizarNombreDTO;
import com.dev.nequi.application.dto.AddProductoRequestDTO;
import com.dev.nequi.application.dto.UpdateStockRequestDTO;
import com.dev.nequi.domain.model.Producto;
import com.dev.nequi.domain.model.Sucursal;
import com.dev.nequi.infrastructure.repository.ProductoRepository;
import com.dev.nequi.infrastructure.repository.SucursalRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Sucursal sucursal;
    private Producto producto;
    private AddProductoRequestDTO addProductoDTO;
    private UpdateStockRequestDTO updateStockDTO;
    private ActualizarNombreDTO actualizarNombreDTO;

    @BeforeEach
    void setUp() {
        sucursal = Sucursal.builder()
                .id("s1")
                .nombre("Sucursal A")
                .franquiciaId("f1")
                .build();

        producto = Producto.builder()
                .id("p1")
                .nombre("Producto A")
                .stock(10)
                .sucursalId("s1")
                .franquiciaId("f1")
                .build();

        addProductoDTO = new AddProductoRequestDTO("s1","Producto A", 10 );
        updateStockDTO = new UpdateStockRequestDTO(50);
        actualizarNombreDTO = new ActualizarNombreDTO("Producto Actualizado");
    }

    @Test
    void addProducto_shouldSaveProductoIfSucursalExists() {
        when(sucursalRepository.findById("s1")).thenReturn(Mono.just(sucursal));
        when(productoRepository.save(any(Producto.class))).thenReturn(Mono.just(producto));

        StepVerifier.create(productoService.addProducto(addProductoDTO))
                .expectNextMatches(p -> p.getNombre().equals("Producto A"))
                .verifyComplete();
    }

    @Test
    void addProducto_shouldFailIfSucursalNotFound() {
        when(sucursalRepository.findById("s1")).thenReturn(Mono.empty());

        StepVerifier.create(productoService.addProducto(addProductoDTO))
                .expectErrorMatches(e -> e.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }

    @Test
    void deleteProducto_shouldReturnDeletedProducto() {
        when(productoRepository.findById("p1")).thenReturn(Mono.just(producto));
        when(productoRepository.delete(producto)).thenReturn(Mono.empty());

        StepVerifier.create(productoService.deleteProducto("p1"))
                .expectNext(producto)
                .verifyComplete();
    }

    @Test
    void deleteProducto_shouldFailIfNotFound() {
        when(productoRepository.findById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productoService.deleteProducto("p1"))
                .expectErrorMatches(e -> e.getMessage().equals("Producto no encontrado"))
                .verify();
    }

    @Test
    void updateProductoStock_shouldUpdateStockIfFound() {
        when(productoRepository.findById("p1")).thenReturn(Mono.just(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(
                Mono.just(Producto.builder().stock(50).build()));

        StepVerifier.create(productoService.updateProductoStock("p1", updateStockDTO))
                .expectNextMatches(p -> p.getStock() == 50)
                .verifyComplete();
    }

    @Test
    void updateProductoStock_shouldFailIfNotFound() {
        when(productoRepository.findById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productoService.updateProductoStock("p1", updateStockDTO))
                .expectErrorMatches(e -> e.getMessage().equals("Producto no encontrado"))
                .verify();
    }

    @Test
    void updateProductoName_shouldUpdateNameIfFound() {
        when(productoRepository.findById("p1")).thenReturn(Mono.just(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(
                Mono.just(Producto.builder().nombre("Producto Actualizado").build()));

        StepVerifier.create(productoService.updateProductoName("p1", actualizarNombreDTO))
                .expectNextMatches(p -> p.getNombre().equals("Producto Actualizado"))
                .verifyComplete();
    }

    @Test
    void updateProductoName_shouldFailIfNotFound() {
        when(productoRepository.findById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productoService.updateProductoName("p1", actualizarNombreDTO))
                .expectErrorMatches(e -> e.getMessage().equals("Producto no encontrado"))
                .verify();
    }

    @Test
    void getProductoById_shouldReturnProductoIfExists() {
        when(productoRepository.findById("p1")).thenReturn(Mono.just(producto));

        StepVerifier.create(productoService.getProductoById("p1"))
                .expectNext(producto)
                .verifyComplete();
    }

    @Test
    void getProductoById_shouldFailIfNotFound() {
        when(productoRepository.findById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productoService.getProductoById("p1"))
                .expectErrorMatches(e -> e.getMessage().equals("Producto no encontrado"))
                .verify();
    }

    @Test
    void getAllProductos_shouldReturnList() {
        when(productoRepository.findAll()).thenReturn(Flux.just(producto));

        StepVerifier.create(productoService.getAllProductos())
                .expectNextMatches(list -> list.size() == 1 && list.get(0).equals(producto))
                .verifyComplete();
    }

    @Test
    void getAllProductos_shouldFailIfEmpty() {
        when(productoRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(productoService.getAllProductos())
                .expectErrorMatches(e -> e.getMessage().equals("No se encontraron productos"))
                .verify();
    }

    @Test
    void obtenerProductoConMasStockPorSucursal_shouldReturnGroupedDTOs() {
        Producto producto2 = Producto.builder()
                                .id("p2")
                                .sucursalId("s1")
                                .stock(100)
                                .franquiciaId("f1")
                                .nombre("Producto B")
                                .build();
        when(productoRepository.findByFranquiciaId("f1")).thenReturn(Flux.just(producto, producto2));
        when(sucursalRepository.findById("s1")).thenReturn(Mono.just(sucursal));

        StepVerifier.create(productoService.obtenerProductoConMasStockPorSucursal("f1"))
                .expectNextMatches(list ->
                        list.size() == 1 &&
                        list.get(0).productoId().equals("p2") &&
                        list.get(0).stock() == 100)
                .verifyComplete();
    }

    @Test
    void obtenerProductoConMasStockPorSucursal_shouldSkipEmptySucursal() {
        Producto producto2 = Producto.builder()
                                .id("p2")
                                .stock(100)
                                .sucursalId("s1")
                                .franquiciaId("f1")
                                .nombre("Producto X")
                                .build();
        when(productoRepository.findByFranquiciaId("f1")).thenReturn(Flux.just(producto2));
        when(sucursalRepository.findById("s1")).thenReturn(Mono.empty());

        StepVerifier.create(productoService.obtenerProductoConMasStockPorSucursal("f1"))
                .expectErrorMatches(e -> e.getMessage().equals("Sucursal no encontrada"))
                .verify();
    }
}

