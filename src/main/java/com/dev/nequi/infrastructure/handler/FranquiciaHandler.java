package com.dev.nequi.infrastructure.handler;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dev.nequi.application.dto.ActualizarNombreDTO;
import com.dev.nequi.application.dto.FranquiciaRequestDTO;
import com.dev.nequi.application.dto.ProductoRequestDTO;
import com.dev.nequi.application.dto.SucursalRequestDTO;
import com.dev.nequi.application.dto.UpdateStockRequestDTO;
import com.dev.nequi.domain.service.FranquiciaService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranquiciaHandler {
    private final FranquiciaService franquiciaService;
    
    public Mono<ServerResponse> crearFranquicia(ServerRequest request) {
        return request.bodyToMono(FranquiciaRequestDTO.class)
            .flatMap(franquiciaService::crearFranquicia)
            .flatMap(franquicia -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franquicia))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage())
            );
    }

    public Mono<ServerResponse> agregarSucursal(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");

        return request.bodyToMono(SucursalRequestDTO.class)
            .flatMap(dto -> franquiciaService.agregarSucursal(franquiciaId, dto))
            .flatMap(fr -> ServerResponse.ok().
                contentType(MediaType.APPLICATION_JSON).
                bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> agregarProducto(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
        String sucursalNombre = request.pathVariable("sucursal");

        return request.bodyToMono(ProductoRequestDTO.class)
            .flatMap(dto -> franquiciaService.agregarProducto(franquiciaId, sucursalNombre, dto))
            .flatMap(fr -> ServerResponse.ok().
                contentType(MediaType.APPLICATION_JSON).
                bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> eliminarProducto(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
        String sucursalId = request.pathVariable("sucursalId");
        String productoId = request.pathVariable("productoId");
    
        return franquiciaService.eliminarProducto(franquiciaId, sucursalId, productoId)
            .flatMap(fr -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> actualizarStock(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
        String sucursalId = request.pathVariable("sucursalId");
        String productoId = request.pathVariable("productoId");
    
        return request.bodyToMono(UpdateStockRequestDTO.class)
            .flatMap(dto -> franquiciaService.actualizarStockProducto(franquiciaId, sucursalId, productoId, dto.stock()))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> obtenerProductosConMasStockPorSucursal(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
    
        var response = franquiciaService.obtenerProductoConMasStockPorSucursal(franquiciaId)
            .flatMap(list -> ServerResponse.ok().bodyValue(list))
            .onErrorResume(e -> {
                System.out.println(e);
                return ServerResponse.status(HttpStatusCode.valueOf(500)).bodyValue("Internal Server Error");
            });
        return response;
    }
    
    
    public Mono<ServerResponse> actualizarNombreFranquicia(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
    
        return request.bodyToMono(ActualizarNombreDTO.class)
            .flatMap(dto -> franquiciaService.actualizarNombreFranquicia(franquiciaId, dto.nuevoNombre()))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> actualizarNombreSucursal(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
        String sucursalId = request.pathVariable("sucursalId");
    
        return request.bodyToMono(ActualizarNombreDTO.class)
            .flatMap(dto -> franquiciaService.actualizarNombreSucursal(franquiciaId, sucursalId, dto.nuevoNombre()))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    
    public Mono<ServerResponse> actualizarNombreProducto(ServerRequest request) {
        String franquiciaId = request.pathVariable("id");
        String sucursalId = request.pathVariable("sucursalId");
        String productoId = request.pathVariable("productoId");
    
        return request.bodyToMono(ActualizarNombreDTO.class)
            .flatMap(dto -> franquiciaService.actualizarNombreProducto(franquiciaId, sucursalId, productoId, dto.nuevoNombre()))
            .flatMap(fr -> ServerResponse.ok().bodyValue(fr))
            .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
    

}
