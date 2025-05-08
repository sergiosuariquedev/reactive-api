package com.dev.nequi.domain.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.application.dto.ProductoMaxStockResponseDTO;
import com.dev.nequi.application.dto.ProductoRequestDTO;
import com.dev.nequi.application.dto.SucursalRequestDTO;
import com.dev.nequi.domain.model.Franquicia;
import com.dev.nequi.domain.model.Producto;
import com.dev.nequi.domain.model.Sucursal;
import com.dev.nequi.infrastructure.repository.FranquiciaRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FranquiciaServiceTest {
    @Mock
    private FranquiciaRepository franquiciaRepository;

    @InjectMocks
    private FranquiciaService franquiciaService;

    private Franquicia franquicia;
    private Sucursal sucursal;
    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Producto 1")
                .stock(10)
                .build();

        sucursal = Sucursal.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Sucursal 1")
                .productos(new ArrayList<>(List.of(producto)))
                .build();

        franquicia = Franquicia.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Franquicia 1")
                .sucursales(new ArrayList<>(List.of(sucursal)))
                .build();
    }

    @Test
    void testActualizarNombreFranquicia() {
        String nuevoNombre = "Franquicia Actualizada";
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.actualizarNombreFranquicia(franquicia.getId(), nuevoNombre))
                .assertNext(f -> assertAll(
                    () -> assertEquals(nuevoNombre, f.getNombre(), "Nombre no actualizado"),
                    () -> assertNotNull(f.getId(), "ID no debería ser nulo")
                ))
                .verifyComplete();

        // Verificación de interacciones
        verify(franquiciaRepository).findById(franquicia.getId());
        verify(franquiciaRepository).save(franquicia);
    }

    @Test
    void testActualizarNombreProducto() {
        
        String nuevoNombre = "Producto Actualizado";
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.actualizarNombreProducto(
                franquicia.getId(), sucursal.getId(), producto.getId(), nuevoNombre))
                .assertNext(updatedFranquicia -> {
                    assertAll(
                        () -> assertNotNull(updatedFranquicia, "La franquicia no debería ser nula"),
                        () -> assertEquals(franquicia.getId(), updatedFranquicia.getId(), 
                            "El ID de franquicia no debería cambiar"),
                        
                        // Verificamos la sucursal
                        () -> assertFalse(updatedFranquicia.getSucursales().isEmpty(), 
                            "Debería tener sucursales"),
                        () -> assertEquals(sucursal.getId(), updatedFranquicia.getSucursales().get(0).getId(),
                            "ID de sucursal incorrecto"),
                        
                        // Verificamos el producto actualizado
                        () -> assertFalse(updatedFranquicia.getSucursales().get(0).getProductos().isEmpty(),
                            "Debería tener productos"),
                        () -> assertEquals(producto.getId(), 
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getId(),
                            "ID de producto incorrecto"),
                        () -> assertEquals(nuevoNombre, 
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getNombre(),
                            "El nombre del producto no se actualizó correctamente"),
                        () -> assertEquals(producto.getStock(),
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getStock(),
                            "El stock no debería modificarse")
                    );
                })
                .verifyComplete();

        // Verificaciones de interacción con el repositorio
        verify(franquiciaRepository).findById(franquicia.getId());
        verify(franquiciaRepository).save(franquicia);
    }

    @Test
    void testActualizarNombreSucursal() {
        String nuevoNombre = "Sucursal Actualizada";
        String nombreOriginal = sucursal.getNombre();
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.actualizarNombreSucursal(
                franquicia.getId(), sucursal.getId(), nuevoNombre))
                .assertNext(updatedFranquicia -> {
                    assertAll(
                        // Validaciones de la franquicia
                        () -> assertNotNull(updatedFranquicia, "La franquicia no debería ser nula"),
                        () -> assertEquals(franquicia.getId(), updatedFranquicia.getId(), 
                            "El ID de franquicia no debería cambiar"),
                        
                        // Validaciones de las sucursales
                        () -> assertFalse(updatedFranquicia.getSucursales().isEmpty(), 
                            "Debería contener sucursales"),
                        () -> assertEquals(1, updatedFranquicia.getSucursales().size(),
                            "No debería cambiar el número de sucursales"),
                        
                        // Validaciones de la sucursal específica
                        () -> assertEquals(sucursal.getId(), updatedFranquicia.getSucursales().get(0).getId(),
                            "El ID de sucursal no debería cambiar"),
                        () -> assertEquals(nuevoNombre, updatedFranquicia.getSucursales().get(0).getNombre(),
                            "El nombre de la sucursal no se actualizó correctamente"),
                        
                        // Validaciones de inmutabilidad
                        () -> assertEquals(sucursal.getProductos(), updatedFranquicia.getSucursales().get(0).getProductos(),
                            "Los productos no deberían modificarse"),
                        () -> assertNotEquals(nombreOriginal, updatedFranquicia.getSucursales().get(0).getNombre(),
                            "El nombre original debería haber cambiado")
                    );
                })
                .verifyComplete();

        // Verificaciones de interacción
        verify(franquiciaRepository, times(1)).findById(franquicia.getId());
        verify(franquiciaRepository, times(1)).save(franquicia);
        verifyNoMoreInteractions(franquiciaRepository);
    }

    @Test
    void testActualizarStockProducto() {
        int nuevoStock = 20;
        int stockOriginal = producto.getStock();
        String nombreProductoOriginal = producto.getNombre();
        
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.actualizarStockProducto(
                franquicia.getId(), sucursal.getId(), producto.getId(), nuevoStock))
                .assertNext(updatedFranquicia -> {
                    assertAll(
                        // Validaciones de la estructura de la franquicia
                        () -> assertNotNull(updatedFranquicia, "La franquicia no debería ser nula"),
                        () -> assertEquals(franquicia.getId(), updatedFranquicia.getId(),
                            "El ID de franquicia no debería cambiar"),
                        
                        // Validaciones de las sucursales
                        () -> assertFalse(updatedFranquicia.getSucursales().isEmpty(),
                            "Debería contener sucursales"),
                        () -> assertEquals(1, updatedFranquicia.getSucursales().size(),
                            "No debería cambiar el número de sucursales"),
                        
                        // Validaciones de la sucursal específica
                        () -> assertEquals(sucursal.getId(), updatedFranquicia.getSucursales().get(0).getId(),
                            "El ID de sucursal no debería cambiar"),
                        
                        // Validaciones de los productos
                        () -> assertFalse(updatedFranquicia.getSucursales().get(0).getProductos().isEmpty(),
                            "Debería contener productos"),
                        () -> assertEquals(1, updatedFranquicia.getSucursales().get(0).getProductos().size(),
                            "No debería cambiar el número de productos"),
                        
                        // Validaciones del producto específico
                        () -> assertEquals(producto.getId(), 
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getId(),
                            "El ID de producto no debería cambiar"),
                        () -> assertEquals(nuevoStock,
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getStock(),
                            "El stock no se actualizó correctamente"),
                        () -> assertEquals(nombreProductoOriginal,
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getNombre(),
                            "El nombre del producto no debería cambiar"),
                        () -> assertNotEquals(stockOriginal,
                            updatedFranquicia.getSucursales().get(0).getProductos().get(0).getStock(),
                            "El stock original debería haber cambiado")
                    );
                })
                .verifyComplete();

        // Verificaciones de interacción
        verify(franquiciaRepository, times(1)).findById(franquicia.getId());
        verify(franquiciaRepository, times(1)).save(franquicia);
        verifyNoMoreInteractions(franquiciaRepository);
    }

    @Test
    void testAgregarProducto() {
        ProductoRequestDTO dto = new ProductoRequestDTO("Nuevo Producto", 5);
        int cantidadProductosOriginal = sucursal.getProductos().size();
        
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.agregarProducto(franquicia.getId(), sucursal.getId(), dto))
                .assertNext(updatedFranquicia -> {
                    assertAll(
                        // Validaciones de estructura
                        () -> assertNotNull(updatedFranquicia, "La franquicia no debería ser nula"),
                        () -> assertEquals(franquicia.getId(), updatedFranquicia.getId(),
                            "El ID de franquicia no debería cambiar"),
                        
                        // Validaciones de sucursales
                        () -> assertFalse(updatedFranquicia.getSucursales().isEmpty(),
                            "Debería contener sucursales"),
                        () -> assertEquals(1, updatedFranquicia.getSucursales().size(),
                            "No debería cambiar el número de sucursales"),
                        
                        // Validaciones de productos
                        () -> assertFalse(updatedFranquicia.getSucursales().get(0).getProductos().isEmpty(),
                            "Debería contener productos"),
                        () -> assertEquals(cantidadProductosOriginal + 1, 
                            updatedFranquicia.getSucursales().get(0).getProductos().size(),
                            "Debería incrementarse la cantidad de productos"),
                        
                        // Validaciones del nuevo producto
                        () -> {
                            Producto nuevoProducto = updatedFranquicia.getSucursales().get(0).getProductos()
                                .stream()
                                .filter(p -> p.getNombre().equals(dto.nombre()))
                                .findFirst()
                                .orElse(null);
                            
                            assertNotNull(nuevoProducto, "El nuevo producto no se encontró");
                            assertEquals(dto.nombre(), nuevoProducto.getNombre(), 
                                "El nombre del producto no coincide");
                            assertEquals(dto.stock(), nuevoProducto.getStock(),
                                "El stock del producto no coincide");
                            assertNotNull(nuevoProducto.getId(), 
                                "El producto debería tener un ID asignado");
                        },
                        
                        // Validación del producto original
                        () -> assertTrue(updatedFranquicia.getSucursales().get(0).getProductos()
                            .stream()
                            .anyMatch(p -> p.getId().equals(producto.getId())),
                            "El producto original no se encuentra")
                    );
                })
                .verifyComplete();

        // Verificaciones de interacción
        verify(franquiciaRepository, times(1)).findById(franquicia.getId());
        verify(franquiciaRepository, times(1)).save(franquicia);
        verifyNoMoreInteractions(franquiciaRepository);
    }

    @Test
    void testAgregarSucursal() {
        SucursalRequestDTO dto = new SucursalRequestDTO("Nueva Sucursal", new ArrayList<>());
        int cantidadSucursalesOriginal = franquicia.getSucursales().size();
        
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.agregarSucursal(franquicia.getId(), dto))
                .assertNext(updatedFranquicia -> {
                    assertAll(
                        // Validaciones de estructura básica
                        () -> assertNotNull(updatedFranquicia, "La franquicia no debería ser nula"),
                        () -> assertEquals(franquicia.getId(), updatedFranquicia.getId(),
                            "El ID de franquicia no debería cambiar"),
                        
                        // Validaciones de sucursales
                        () -> assertFalse(updatedFranquicia.getSucursales().isEmpty(),
                            "Debería contener sucursales"),
                        () -> assertEquals(cantidadSucursalesOriginal + 1, 
                            updatedFranquicia.getSucursales().size(),
                            "Debería incrementarse la cantidad de sucursales"),
                        
                        // Validaciones de la nueva sucursal
                        () -> {
                            Sucursal nuevaSucursal = updatedFranquicia.getSucursales().get(1);
                            assertNotNull(nuevaSucursal, "La nueva sucursal no debería ser nula");
                            assertEquals(dto.nombre(), nuevaSucursal.getNombre(),
                                "El nombre de la sucursal no coincide");
                            assertNotNull(nuevaSucursal.getId(),
                                "La sucursal debería tener un ID asignado");
                            assertTrue(nuevaSucursal.getProductos().isEmpty(),
                                "La sucursal debería iniciar sin productos");
                        },
                        
                        // Validación de la sucursal original
                        () -> assertEquals(sucursal.getId(), 
                            updatedFranquicia.getSucursales().get(0).getId(),
                            "La sucursal original no se encuentra o cambió de posición"),
                        () -> assertEquals(sucursal.getProductos().size(),
                            updatedFranquicia.getSucursales().get(0).getProductos().size(),
                            "Los productos de la sucursal original no deberían modificarse")
                    );
                })
                .verifyComplete();

        // Verificaciones de interacción
        verify(franquiciaRepository, times(1)).findById(franquicia.getId());
        verify(franquiciaRepository, times(1)).save(franquicia);
        verifyNoMoreInteractions(franquiciaRepository);
    }

    @Test
    void testCrearFranquicia() {
        FranquiciaRequestDTO dto = new FranquiciaRequestDTO("Nueva Franquicia", new ArrayList<>());
        when(franquiciaRepository.save(any(Franquicia.class))).thenAnswer(invocation -> {
            Franquicia franquiciaToSave = invocation.getArgument(0);
            Franquicia savedFranquicia = Franquicia.builder()
                    .id("1")
                    .nombre(franquiciaToSave.getNombre())
                    .sucursales(franquiciaToSave.getSucursales())
                    .build();
            return Mono.just(savedFranquicia);
        });

        StepVerifier.create(franquiciaService.crearFranquicia(dto))
                .assertNext(createdFranquicia -> {
                    assertAll(
                        () -> assertNotNull(createdFranquicia, "La franquicia creada no debería ser nula"),
                        () -> assertEquals("1", createdFranquicia.getId(), 
                            "El ID generado no coincide"),
                        () -> assertEquals(dto.nombre(), createdFranquicia.getNombre(), 
                            "El nombre no coincide con el DTO"),
                        
                        () -> assertNotNull(createdFranquicia.getSucursales(), 
                            "La lista de sucursales no debería ser nula"),
                        () -> assertTrue(createdFranquicia.getSucursales().isEmpty(), 
                            "La franquicia debería crearse sin sucursales"),
                        
                        () -> assertNotSame(dto, createdFranquicia,
                            "Debería ser una nueva instancia, no el DTO original"),
                        
                        () -> assertInstanceOf(Franquicia.class, createdFranquicia,
                            "El objeto retornado debería ser una Franquicia")
                    );
                })
                .verifyComplete();

        verify(franquiciaRepository, times(1)).save(any(Franquicia.class));
        verifyNoMoreInteractions(franquiciaRepository);
    }

    

    @Test
    void testEliminarProducto() {
        int cantidadProductosOriginal = sucursal.getProductos().size();
        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));
        when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.eliminarProducto(
                franquicia.getId(), sucursal.getId(), producto.getId()))
                .assertNext(updatedFranquicia -> {
                    assertAll(
                        // Validaciones de estructura básica
                        () -> assertNotNull(updatedFranquicia, "La franquicia no debería ser nula"),
                        () -> assertEquals(franquicia.getId(), updatedFranquicia.getId(),
                            "El ID de franquicia no debería cambiar"),
                        
                        // Validaciones de sucursales
                        () -> assertFalse(updatedFranquicia.getSucursales().isEmpty(),
                            "Debería contener sucursales"),
                        () -> assertEquals(1, updatedFranquicia.getSucursales().size(),
                            "No debería cambiar el número de sucursales"),
                        
                        // Validaciones de productos
                        () -> assertEquals(cantidadProductosOriginal - 1,
                            updatedFranquicia.getSucursales().get(0).getProductos().size(),
                            "Debería disminuir la cantidad de productos"),
                        
                        // Validación de que el producto fue eliminado
                        () -> assertTrue(updatedFranquicia.getSucursales().get(0).getProductos()
                            .stream()
                            .noneMatch(p -> p.getId().equals(producto.getId())),
                            "El producto no fue eliminado correctamente"),
                        
                        // Validación de que la sucursal se mantiene igual
                        () -> assertEquals(sucursal.getId(), 
                            updatedFranquicia.getSucursales().get(0).getId(),
                            "El ID de sucursal no debería cambiar")
                    );
                })
                .verifyComplete();

        // Verificaciones de interacción
        verify(franquiciaRepository, times(1)).findById(franquicia.getId());
        verify(franquiciaRepository, times(1)).save(franquicia);
        verifyNoMoreInteractions(franquiciaRepository);
    }

    @Test
    void testObtenerProductoConMasStockPorSucursal() {
        Producto producto2 = Producto.builder()
                .id(UUID.randomUUID().toString())
                .nombre("Producto 2")
                .stock(5)
                .build();
        sucursal.getProductos().add(producto2);

        when(franquiciaRepository.findById(franquicia.getId())).thenReturn(Mono.just(franquicia));

        StepVerifier.create(franquiciaService.obtenerProductoConMasStockPorSucursal(franquicia.getId()))
                .assertNext(resultList -> {
                    assertAll(
                        // Validaciones básicas de la lista
                        () -> assertNotNull(resultList, "La lista de resultados no debería ser nula"),
                        () -> assertEquals(1, resultList.size(), 
                            "Debería retornar un resultado por sucursal"),
                        
                        // Validaciones del resultado
                        () -> {
                            ProductoMaxStockResponseDTO result = resultList.get(0);
                            assertAll(
                                () -> assertEquals(sucursal.getId(), result.sucursalId(),
                                    "ID de sucursal incorrecto"),
                                () -> assertEquals(sucursal.getNombre(), result.sucursalNombre(),
                                    "Nombre de sucursal incorrecto"),
                                () -> assertEquals(producto.getId(), result.productoId(),
                                    "ID de producto incorrecto"),
                                () -> assertEquals(producto.getNombre(), result.productoNombre(),
                                    "Nombre de producto incorrecto"),
                                () -> assertEquals(producto.getStock(), result.stock(),
                                    "Stock incorrecto"),
                                () -> assertTrue(result.stock() > producto2.getStock(),
                                    "Debería retornar el producto con mayor stock")
                            );
                        },
                        
                        // Validación adicional de ordenamiento
                        () -> assertTrue(resultList.stream()
                            .allMatch(r -> r.stock() >= producto2.getStock()),
                            "Todos los productos retornados deben tener stock mayor o igual")
                    );
                })
                .verifyComplete();

        verify(franquiciaRepository, times(1)).findById(franquicia.getId());
        verifyNoMoreInteractions(franquiciaRepository);
    }

    @Test
    void testObtenerTodasLasFranquicias() {
        List<Franquicia> franquicias = List.of(franquicia);
        when(franquiciaRepository.findAll()).thenReturn(Flux.fromIterable(franquicias));
    
        StepVerifier.create(franquiciaService.obtenerTodasLasFranquicias())
                .assertNext(franquiciaList -> {
                    assertAll(
                        // Validaciones básicas
                        () -> assertNotNull(franquiciaList, "La lista de franquicias no debería ser nula"),
                        () -> assertEquals(1, franquiciaList.size(), 
                            "Debería retornar exactamente una franquicia"),
                        
                        // Validaciones de la franquicia retornada
                        () -> {
                            Franquicia returnedFranquicia = franquiciaList.get(0);
                            assertAll(
                                () -> assertEquals(franquicia.getId(), returnedFranquicia.getId(),
                                    "El ID de franquicia no coincide"),
                                () -> assertEquals(franquicia.getNombre(), returnedFranquicia.getNombre(),
                                    "El nombre de franquicia no coincide"),
                                () -> assertNotNull(returnedFranquicia.getSucursales(),
                                    "La lista de sucursales no debería ser nula"),
                                () -> assertEquals(franquicia.getSucursales().size(), 
                                    returnedFranquicia.getSucursales().size(),
                                    "El número de sucursales no coincide")
                            );
                        }
                    );
                })
                .verifyComplete();
    
        verify(franquiciaRepository, times(1)).findAll();
        verifyNoMoreInteractions(franquiciaRepository);
    }
}
