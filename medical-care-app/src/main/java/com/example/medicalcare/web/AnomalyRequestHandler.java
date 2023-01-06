package com.example.medicalcare.web;

import com.example.medicalcare.model.Anomaly;
import com.example.medicalcare.repository.AnomalyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class AnomalyRequestHandler {

    @Autowired
    private AnomalyRepository anomalyRepository;

    Mono<ServerResponse> handleAnomaly(ServerRequest r) {
        var limit = Integer.parseInt(r.queryParam("limit").orElse("1000"));
        var status = r.queryParam("status");
        var severity = r.queryParam("severity");
        log.info("limit = {}", limit);
        log.info("status = {}", status);
        log.info("severity = {}", severity);
        if (!status.isPresent() && !severity.isPresent())
            return ok()
                    .body(BodyInserters.fromPublisher(
                            anomalyRepository
                            .findAll()
                            .take(limit), Anomaly.class)
                    )
                    .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                    .flatMap(s -> ServerResponse.ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .bodyValue(s)));
        else if (!status.isPresent() && severity.isPresent()) {
            if (severity.get().equals("moderate"))
                return ok()
                        .body(BodyInserters.fromPublisher(anomalyRepository
                                .findBySeverity(Anomaly.Severity.MODERATE)
                                .take(limit), Anomaly.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else if  (severity.get().equals("severe"))
                return ok()
                        .body(BodyInserters.fromPublisher(anomalyRepository
                                .findBySeverity(Anomaly.Severity.SEVERE)
                                .take(limit), Anomaly.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .body(BodyInserters.fromPublisher(anomalyRepository
                                .findBySeverity(Anomaly.Severity.CRITICAL)
                                .take(limit), Anomaly.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else if (status.isPresent() && !severity.isPresent()) {
            if (status.get().equals("ongoing"))
                return ok()
                        .body(BodyInserters.fromPublisher(anomalyRepository
                                .findByStatus(Anomaly.Status.ONGOING)
                                .take(limit), Anomaly.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .body(BodyInserters.fromPublisher(anomalyRepository
                                .findByStatus(Anomaly.Status.CLOSED)
                                .take(limit), Anomaly.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else {
            return ok()
                    .body(BodyInserters.fromPublisher(anomalyRepository
                            .findBySeverityAndStatus(
                                    Anomaly.Severity.valueOf(severity.get()),
                                    Anomaly.Status.valueOf(status.get()))
                            .take(limit), Anomaly.class))
                    .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                            .flatMap(s -> ServerResponse.ok()
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue(s)));
        }
    }
}
