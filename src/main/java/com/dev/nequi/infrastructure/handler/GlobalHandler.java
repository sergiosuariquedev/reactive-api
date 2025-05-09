package com.dev.nequi.infrastructure.handler;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dev.nequi.application.dto.ActualizarNombreDTO;
import com.dev.nequi.application.dto.AddProductoRequestDTO;
import com.dev.nequi.application.dto.AddSucursalRequestDTO;
import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.application.dto.UpdateStockRequestDTO;
import com.dev.nequi.domain.service.FranquiciaService;
import com.dev.nequi.domain.service.ProductoService;
import com.dev.nequi.domain.service.SucursalService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GlobalHandler {
    private final FranquiciaService franquiciaService;
    private final SucursalService sucursalService;
    private final ProductoService  productoService;
    
    public Mono<ServerResponse> crearFranquicia(ServerRequest request) {
        return request.bodyToMono(FranquiciaRequestDTO.class)
            .flatMap(franquiciaService::createFranquicia)
            .flatMap(franquicia -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franquicia))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage())
            );
    }

    public Mono<ServerResponse> agregarSucursal(ServerRequest request) {
        return request.bodyToMono(AddSucursalRequestDTO.class)
            .flatMap(sucursalService::addSucursal)
            .flatMap(fr -> ServerResponse.ok().
                contentType(MediaType.APPLICATION_JSON).
                bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> agregarProducto(ServerRequest request) {
        return request.bodyToMono(AddProductoRequestDTO.class)
            .flatMap(productoService::addProducto)
            .flatMap(fr -> ServerResponse.ok().
                contentType(MediaType.APPLICATION_JSON).
                bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> eliminarProducto(ServerRequest request) {
        String productoId = request.pathVariable("productoId");
    
        return productoService.deleteProducto(productoId)
            .flatMap(fr -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> actualizarStock(ServerRequest request) {
        String productoId = request.pathVariable("productoId");
        return request.bodyToMono(UpdateStockRequestDTO.class)
            .flatMap(dto -> productoService.updateProductoStock(productoId, dto))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> obtenerProductosConMasStockPorSucursal(ServerRequest request) {
        String franquiciaId = request.pathVariable("franquiciaId");
        System.out.println(franquiciaId);
        return productoService.obtenerProductoConMasStockPorSucursal(franquiciaId)
            .flatMap(list -> ServerResponse.ok().bodyValue(list))
            .onErrorResume(e -> {
                return ServerResponse.status(HttpStatusCode.valueOf(500)).bodyValue("Internal Server Error");
            });
    }
    
    
    public Mono<ServerResponse> actualizarNombreFranquicia(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
    
        return request.bodyToMono(ActualizarNombreDTO.class)
            .flatMap(dto -> franquiciaService.updateFranquiciaName(franquiciaId, dto.nuevoNombre()))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> actualizarNombreSucursal(ServerRequest request) {
        String sucursalId = request.pathVariable("sucursalId");
    
        return request.bodyToMono(ActualizarNombreDTO.class)
            .flatMap(dto -> sucursalService.updateSucursalNameById(sucursalId, dto))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> actualizarNombreProducto(ServerRequest request) {
        String productoId = request.pathVariable("productoId");
    
        return request.bodyToMono(ActualizarNombreDTO.class)
            .flatMap(dto -> productoService.updateProductoName(productoId, dto))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> obtenerTodasLasFranquicias(ServerRequest request) {
        return franquiciaService.getAllFranquicias()
            .flatMap(franquicias -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franquicias))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> obtenerTodasLasSucursales(ServerRequest request) {
        return sucursalService.getAllSucursales()
            .flatMap(sucursales -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sucursales))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
}
