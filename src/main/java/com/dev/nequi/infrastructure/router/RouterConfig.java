package com.dev.nequi.infrastructure.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dev.nequi.infrastructure.handler.GlobalHandler;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> franquiciasRoutes(GlobalHandler handler) {
        return RouterFunctions.route()
            .POST("/franquicias", handler::crearFranquicia)
            .GET("/franquicias", handler::obtenerTodasLasFranquicias)
            .PUT("/franquicias/{id}/nombre", handler::actualizarNombreFranquicia)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> sucursalRoutes(GlobalHandler handler) {
        return RouterFunctions.route()
            .POST("/sucursales", handler::agregarSucursal)
            .GET("/sucursales", handler::obtenerTodasLasSucursales)
            .PUT("/sucursales/{sucursalId}/nombre", handler::actualizarNombreSucursal)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productoRoutes(GlobalHandler handler) {
        return RouterFunctions.route()
            .POST("/productos", handler::agregarProducto)
            .PUT("/productos/{productoId}/stock", handler::actualizarStock)
            .PUT("/productos/{productoId}/nombre", handler::actualizarNombreProducto)
            .DELETE("/productos/{productoId}", handler::eliminarProducto)
            .GET("/productos/max-stock/{franquiciaId}", handler::obtenerProductosConMasStockPorSucursal)
            .build();
    }

}
