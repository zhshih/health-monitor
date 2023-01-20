package com.example.healthmonitor.web;

import com.example.healthmonitor.model.AggregatedHealthInfo;
import com.example.healthmonitor.repository.AggregatedHealthInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class HealthInfoRequestHandler {

    private static class OptionalParameters {
        private int limit;
        private LocalDate beginDate;
        private LocalDate endDate;
        public void parseParameters(ServerRequest r) {
            limit = Integer.parseInt(r.queryParam("limit").orElse("200"));
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
    AggregatedHealthInfoRepository aggregatedHealthInfoRepository;

    Mono<ServerResponse> handleHealthInfo(ServerRequest r) {
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        return ok()
                .body(BodyInserters.fromPublisher(
                        aggregatedHealthInfoRepository
                                .findBetweenCreated(optionalParameters.beginDate, optionalParameters.endDate)
                                .take(optionalParameters.limit), AggregatedHealthInfo.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }

    Mono<ServerResponse> handleHealthInfoById(ServerRequest r) {
        var id = r.pathVariable("id");
        OptionalParameters optionalParameters = new OptionalParameters();
        optionalParameters.parseParameters(r);
        log.info("optionalParameters = {}", optionalParameters);
        return ok()
                .body(BodyInserters.fromPublisher(
                        aggregatedHealthInfoRepository
                                .findByPatientIdBetweenCreated(Long.parseLong(id),
                                        optionalParameters.beginDate, optionalParameters.endDate)
                                .take(optionalParameters.limit), AggregatedHealthInfo.class)
                )
                .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                        .flatMap(s -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue(s)));
    }
}
