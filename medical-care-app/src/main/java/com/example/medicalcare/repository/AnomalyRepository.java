package com.example.medicalcare.repository;

import com.example.medicalcare.model.Anomaly;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AnomalyRepository extends ReactiveCrudRepository<Anomaly, String> {
    @Query(value="{'status': ?0}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByStatus(Anomaly.Status status);
    @Query(value="{'severity': ?0}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findBySeverity(Anomaly.Severity severity);
    @Query(value="{'severity': ?0, 'status': ?1}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findBySeverityAndStatus(Anomaly.Severity severity, Anomaly.Status status);
}
