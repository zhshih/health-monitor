package com.example.healthmonitor.repository;

import com.example.healthmonitor.model.Anomaly;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface AnomalyRepository extends ReactiveCrudRepository<Anomaly, String> {

    @Query(value="{'severity': ?0, 'status': ?1, 'issuedDatetime': {$gt: ?2, $lt: ?3}}",
            sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByCriteria(Anomaly.Severity severity, Anomaly.Status status,
                                 LocalDate beginDate, LocalDate endDate);

    @Query(value="{'severity': ?0, 'issuedDatetime': {$gt: ?1, $lt: ?2}}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByCriteria(Anomaly.Severity severity, LocalDate beginDate, LocalDate endDate);

    @Query(value="{'status': ?0, 'issuedDatetime': {$gt: ?1, $lt: ?2}}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByCriteria(Anomaly.Status status, LocalDate beginDate, LocalDate endDate);

    @Query(value="{'issuedDatetime': {$gt: ?0, $lt: ?1}}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByCriteria(LocalDate beginDate, LocalDate endDate);

    @Query(value="{'patientId': ?0, 'severity': ?1, 'status': ?2, 'issuedDatetime': {$gt: ?3, $lt: ?4}}",
            sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByPatientIdAndCriteria(Long id, Anomaly.Severity severity, Anomaly.Status status,
                                  LocalDate beginDate, LocalDate endDate);

    @Query(value="{'patientId': ?0, 'severity': ?1, 'issuedDatetime': {$gt: ?2, $lt: ?3}}",
            sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByPatientIdAndCriteria(Long id, Anomaly.Severity severity,
                                             LocalDate beginDate, LocalDate endDate);

    @Query(value="{'patientId': ?0, 'status': ?1, 'issuedDatetime': {$gt: ?2, $lt: ?3}}",
            sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByPatientIdAndCriteria(Long id, Anomaly.Status status,
                                             LocalDate beginDate, LocalDate endDate);

    @Query(value="{'patientId': ?0, 'issuedDatetime': {$gt: ?1, $lt: ?2}}", sort="{'issuedDatetime': 1}")
    Flux<Anomaly> findByPatientIdAndCriteria(Long id, LocalDate beginDate, LocalDate endDate);
}
