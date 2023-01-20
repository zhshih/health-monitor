package com.example.healthmonitor.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class HealthInfoEndpointConfiguration {
    @Bean
    RouterFunction<ServerResponse> nestedHealthInfo(HealthInfoRequestHandler handler) {
        var requestPredicate = accept(APPLICATION_JSON).or(accept(APPLICATION_JSON_UTF8));
        return route()
                .nest(path("/api/monitor"), builder -> builder
                        .nest(requestPredicate, nestedBuilder -> nestedBuilder
                                .GET("/healthInfo", handler::handleHealthInfo)
                                .GET("/healthInfo/patient/{id}", handler::handleHealthInfoById)
                        )
                )
                .build();
    }
}
