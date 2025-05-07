package com.dev.nequi.infrastructure.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

import com.dev.nequi.infrastructure.handler.FranquiciaHandler;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> router(FranquiciaHandler handler) {
        return route(POST("/franquicias"), handler::crearFranquicia);
    }

    @Bean
    public RouterFunction<ServerResponse> sucursalRoutes(FranquiciaHandler handler) {
        return RouterFunctions.route()
            .POST("/franquicias/{id}/sucursales", handler::agregarSucursal)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productoRoutes(FranquiciaHandler handler) {
        return RouterFunctions.route()
            .POST("/franquicias/{id}/sucursales/{sucursal}/productos", handler::agregarProducto)
            .DELETE("/franquicias/{id}/sucursales/{sucursalId}/productos/{productoId}", handler::eliminarProducto)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> actualizarStockRoute(FranquiciaHandler handler) {
        return RouterFunctions.route()
            .PUT("/franquicias/{id}/sucursales/{sucursalId}/productos/{productoId}/stock", handler::actualizarStock)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productosConMasStockRoute(FranquiciaHandler handler) {
        return RouterFunctions.route()
            .GET("/franquicias/{id}/productos/max-stock", handler::obtenerProductosConMasStockPorSucursal)
            .build();
    }


    @Bean
    public RouterFunction<ServerResponse> actualizarNombreRoutes(FranquiciaHandler handler) {
        return RouterFunctions.route()
            .PUT("/franquicias/{id}/nombre", handler::actualizarNombreFranquicia)
            .PUT("/franquicias/{id}/sucursales/{sucursalId}/nombre", handler::actualizarNombreSucursal)
            .PUT("/franquicias/{id}/sucursales/{sucursalId}/productos/{productoId}/nombre", handler::actualizarNombreProducto)
            .build();
}




}
