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

import static com.example.notification.model.Notification.Severity.*;
import static com.example.notification.model.Notification.Status.CLOSED;
import static com.example.notification.model.Notification.Status.ONGOING;
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
                    .bodyValue(notificationRepository
                            .findAll()
                            .take(limit))
                    .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                    .flatMap(s -> ServerResponse.ok()
                            .contentType(MediaType.TEXT_PLAIN)
                            .bodyValue(s)));
        else if (!status.isPresent() && severity.isPresent()) {
            if (severity.get().equals("moderate"))
                return ok()
                        .bodyValue(notificationRepository.findBySeverity(MODERATE).take(limit))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else if  (severity.get().equals("severe"))
                return ok()
                        .bodyValue(notificationRepository.findBySeverity(SEVERE).take(limit))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .bodyValue(notificationRepository.findBySeverity(CRITICAL).take(limit))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else if (status.isPresent() && !severity.isPresent()) {
            if (status.get().equals("ongoing"))
                return ok()
                        .bodyValue(notificationRepository.findByStatus(ONGOING).take(limit))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
            else
                return ok()
                        .bodyValue(notificationRepository.findByStatus(CLOSED).take(limit))
                        .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .bodyValue(s)));
        }
        else {
            return ok()
                    .bodyValue(notificationRepository.findAll().take(limit))
                    .onErrorResume(e -> Mono.just("Error " + e.getMessage())
                            .flatMap(s -> ServerResponse.ok()
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue(s)));
        }
    }
}
