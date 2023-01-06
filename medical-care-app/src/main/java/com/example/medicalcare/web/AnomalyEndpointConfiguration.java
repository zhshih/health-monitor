package com.example.medicalcare.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AnomalyEndpointConfiguration {
    @Bean
    RouterFunction<ServerResponse> nested(AnomalyRequestHandler handler) {
        var requestPredicate = accept(APPLICATION_JSON).or(accept(APPLICATION_JSON_UTF8));
        return route()
                .nest(path("/api"), builder -> builder
                        .nest(requestPredicate, nestedBuilder -> nestedBuilder
                                .GET("/anomaly", handler::handleAnomaly)
                                .GET("/anomaly", queryParam("limit", t -> true),
                                        handler::handleAnomaly)
                        )
                )
                .build();
    }
}
