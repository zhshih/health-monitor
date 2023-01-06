package com.example.medicalcare.repository;

import com.example.medicalcare.model.Notification;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, String> {
    @Query(value="{'status': ?0}", sort="{'issuedDatetime': 1}")
    Flux<Notification> findByStatus(Notification.Status status);
    @Query(value="{'severity': ?0}", sort="{'issuedDatetime': 1}")
    Flux<Notification> findBySeverity(Notification.Severity severity);
    @Query(value="{'severity': ?0, 'status': ?1}", sort="{'issuedDatetime': 1}")
    Flux<Notification> findBySeverityAndStatus(Notification.Severity severity, Notification.Status status);
}
