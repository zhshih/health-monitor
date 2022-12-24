package com.example.notification.web;

import com.example.notification.model.Notification;
import com.example.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;

import static com.example.notification.model.Notification.Severity.*;
import static com.example.notification.model.Notification.Status.CLOSED;
import static com.example.notification.model.Notification.Status.ONGOING;
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
                    .body(fromPublisher(
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
                        .body(fromPublisher(notificationRepository
                                .findBySeverity(MODERATE)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else if  (severity.get().equals("severe"))
                return ok()
                        .body(fromPublisher(notificationRepository
                                .findBySeverity(SEVERE)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .body(fromPublisher(notificationRepository
                                .findBySeverity(CRITICAL)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else if (status.isPresent() && !severity.isPresent()) {
            if (status.get().equals("ongoing"))
                return ok()
                        .body(fromPublisher(notificationRepository
                                .findByStatus(ONGOING)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .body(fromPublisher(notificationRepository
                                .findByStatus(CLOSED)
                                .take(limit), Notification.class))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else {
            return ok()
                    .body(fromPublisher(notificationRepository
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
