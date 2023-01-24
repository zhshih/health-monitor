package com.example.medicalcare.web;

import com.example.medicalcare.model.Anomaly;
import com.example.medicalcare.model.MedicalInstruction;
import com.example.medicalcare.repository.MedicalInstructionRepository;
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

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class MedicalInstructionRequestHandler {

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
    private MedicalInstructionRepository medicalInstructionRepository;

    Mono<ServerResponse> handleMedicalInstruction(ServerRequest r) {
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        Flux<MedicalInstruction> medicalInstructions;
        if (!r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            medicalInstructions = medicalInstructionRepository
                    .findByCriteria(optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            medicalInstructions = medicalInstructionRepository
                    .findByCriteria(optionalParameters.severity,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (!r.queryParam("severity").isPresent() && r.queryParam("status").isPresent()) {
            medicalInstructions = medicalInstructionRepository
                    .findByCriteria(optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else {
            medicalInstructions = medicalInstructionRepository
                    .findByCriteria(optionalParameters.severity, optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        return ok()
                .body(BodyInserters.fromPublisher(
                        medicalInstructions
                                .take(optionalParameters.limit), MedicalInstruction.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }

    Mono<ServerResponse> handleMedicalInstructionByPatient(ServerRequest r) {
        var id = r.pathVariable("id");
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        Flux<MedicalInstruction> medicalInstructions;
        if (!r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            medicalInstructions = medicalInstructionRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id),
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (r.queryParam("severity").isPresent() && !r.queryParam("status").isPresent()) {
            medicalInstructions = medicalInstructionRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id),
                            optionalParameters.severity,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else if (!r.queryParam("severity").isPresent() && r.queryParam("status").isPresent()) {
            medicalInstructions = medicalInstructionRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id), optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        else {
            medicalInstructions = medicalInstructionRepository
                    .findByPatientIdAndCriteria(Long.parseLong(id),
                            optionalParameters.severity, optionalParameters.status,
                            optionalParameters.beginDate, optionalParameters.endDate);
        }
        return ok()
                .body(BodyInserters.fromPublisher(
                        medicalInstructions
                                .take(optionalParameters.limit), MedicalInstruction.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }

    Mono<ServerResponse> handleMedicalInstructionByAnomaly(ServerRequest r) {
        var id = r.pathVariable("id");
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        return ok()
                .body(BodyInserters.fromPublisher(
                        medicalInstructionRepository
                                .findByAnomalyId(id), MedicalInstruction.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }
}
