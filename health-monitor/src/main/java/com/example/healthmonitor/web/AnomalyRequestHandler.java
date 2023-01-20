package com.example.healthmonitor.web;

import com.example.healthmonitor.model.Anomaly;
import com.example.healthmonitor.repository.AnomalyRepository;
import com.example.healthmonitor.repository.MedicalInstructionRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class AnomalyRequestHandler {

    @Data
    @NoArgsConstructor
    private static class OptionalParameters {
        private int limit;
        private Anomaly.Severity severity;
        private Anomaly.Status status;
        private LocalDate beginDate;
        private LocalDate endDate;
        public void parseParameters(ServerRequest r) {
            limit = Integer.parseInt(r.queryParam("limit").orElse("200"));
            severity = Anomaly.Severity.valueOf(r.queryParam("severity")
                    .orElse(Anomaly.Severity.MODERATE.toString()).toUpperCase());
            status = Anomaly.Status.valueOf(r.queryParam("status")
                    .orElse(Anomaly.Status.ONGOING.toString()).toUpperCase());
            endDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
            var endTimeStr = r.queryParam("endTime");
            if (endTimeStr.isPresent()) {
                try {
                    endDate = LocalDate.parse(endTimeStr.get());
                } catch (DateTimeException e) {
                    log.info("unrecognized endTimeStr = {}", endTimeStr);
                }
            }
            beginDate = endDate.minus(7, ChronoUnit.DAYS);
            var beginTimeStr = r.queryParam("beginTime");
            if (beginTimeStr.isPresent()) {
                try {
                    beginDate = LocalDate.parse(beginTimeStr.get());
                } catch (DateTimeException e) {
                    log.info("unrecognized beginTimeStr = {}", beginTimeStr);
                }
            }
        }
    }

    @Autowired
    AnomalyRepository anomalyRepository;

    @Autowired
    MedicalInstructionRepository medicalInstructionRepository;

    Mono<ServerResponse> handleAnomaly(ServerRequest r) {
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        Flux<Anomaly> anomalies;
        if (!r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            anomalies = anomalyRepository
                    .findByCriteria(optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            anomalies = anomalyRepository
                    .findByCriteria(optionalParameters.severity,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (!r.queryParam("severity").isPresent() && r.queryParam("status").isPresent()) {
            anomalies = anomalyRepository
                    .findByCriteria(optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else {
            anomalies = anomalyRepository
                    .findByCriteria(optionalParameters.severity, optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }

        return ok()
                .body(BodyInserters.fromPublisher(anomalies
                        .take(optionalParameters.limit)
                        .flatMap(this::loadMedicalInstruction), Anomaly.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }

    Mono<ServerResponse> handleAnomalyByPatient(ServerRequest r) {
        var id = r.pathVariable("id");
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        Flux<Anomaly> anomalies;
        if (!r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            anomalies = anomalyRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id),
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            anomalies = anomalyRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id), optionalParameters.severity,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (!r.queryParam("severity").isPresent() && r.queryParam("status").isPresent()) {
            anomalies = anomalyRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id), optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else {
            anomalies = anomalyRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id),
                            optionalParameters.severity, optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }

        return ok()
                .body(BodyInserters.fromPublisher(anomalies
                                .take(optionalParameters.limit)
                                .flatMap(this::loadMedicalInstruction), Anomaly.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }

    private Mono<Anomaly> loadMedicalInstruction(final Anomaly anomaly) {
        Mono<Anomaly> mono = Mono.just(anomaly)
                .zipWith(medicalInstructionRepository.findByAnomalyId(anomaly.getId()))
                .map(result -> {
                    Anomaly tmp = result.getT1();
                    tmp.setMedicalInstruction(result.getT2());
                    return tmp;
                })
                .switchIfEmpty(Mono.just(anomaly));

        log.info("mono = {}", mono);
        return mono;
    }
}
