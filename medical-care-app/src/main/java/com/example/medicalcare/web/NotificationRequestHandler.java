package com.example.medicalcare.web;

import com.example.medicalcare.model.Notification;
import com.example.medicalcare.repository.NotificationRepository;
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
public class NotificationRequestHandler {

    @Autowired
    private NotificationRepository notificationRepository;

    Mono<ServerResponse> handleNotification(ServerRequest r) {
        var limit = Integer.parseInt(r.queryParam("limit").orElse("1000"));
        var status = r.queryParam("status");
        var severity = r.queryParam("severity");
        log.info("limit = {}", limit);
        log.info("status = {}", status);
        log.info("severity = {}", severity);
        if (!status.isPresent() && !severity.isPresent())
            return ok()
                    .body(BodyInserters.fromPublisher(
                            notificationRepository
                            .findAll()
                            .take(limit), Notification.class)
                    )
                    .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                    .flatMap(s -> ServerResponse.ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .bodyValue(s)));
        else if (!status.isPresent() && severity.isPresent()) {
            if (severity.get().equals("moderate"))
                return ok()
                        .body(BodyInserters.fromPublisher(notificationRepository
                                .findBySeverity(Notification.Severity.MODERATE)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else if  (severity.get().equals("severe"))
                return ok()
                        .body(BodyInserters.fromPublisher(notificationRepository
                                .findBySeverity(Notification.Severity.SEVERE)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .body(BodyInserters.fromPublisher(notificationRepository
                                .findBySeverity(Notification.Severity.CRITICAL)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else if (status.isPresent() && !severity.isPresent()) {
            if (status.get().equals("ongoing"))
                return ok()
                        .body(BodyInserters.fromPublisher(notificationRepository
                                .findByStatus(Notification.Status.ONGOING)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .body(BodyInserters.fromPublisher(notificationRepository
                                .findByStatus(Notification.Status.CLOSED)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else {
            return ok()
                    .body(BodyInserters.fromPublisher(notificationRepository
                            .findBySeverityAndStatus(
                                    Notification.Severity.valueOf(severity.get()),
                                    Notification.Status.valueOf(status.get()))
                            .take(limit), Notification.class))
                    .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                            .flatMap(s -> ServerResponse.ok()
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue(s)));
        }
    }
}
