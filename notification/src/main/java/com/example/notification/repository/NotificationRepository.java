package com.example.notification.repository;

import com.example.notification.model.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, String> {
//    Flux<Notification> findAllWithLimit(int limit);
    Flux<Notification> findByStatus(Notification.Status status);
//    Flux<Notification> findByStatusWithLimit(Notification.Status status, int limit);
    Flux<Notification> findBySeverity(Notification.Severity severity);
//    Flux<Notification> findBySeverityWithLimit(Notification.Severity severity, int limit);
//    Mono<Boolean> existsById(String id);
}
