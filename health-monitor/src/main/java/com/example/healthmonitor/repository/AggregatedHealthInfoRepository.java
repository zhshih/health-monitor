package com.example.healthmonitor.repository;

import com.example.healthmonitor.model.AggregatedHealthInfo;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface AggregatedHealthInfoRepository extends ReactiveCrudRepository<AggregatedHealthInfo, String> {
    @Query(value="{'patientId': ?0}", sort="{'createAt': 1}")
    Flux<AggregatedHealthInfo> findByPatientId(Long id);

    @Query(value="{'patientId': ?0, 'createAt': {$gt: ?1, $lt: ?2}}", sort="{'createAt': 1}")
    Flux<AggregatedHealthInfo> findByPatientIdBetweenCreated(Long id, LocalDate beginDatetime, LocalDate endDatetime);

    @Query(value="{'createAt': {$gt: ?0, $lt: ?1}}", sort="{'createAt': 1}")
    Flux<AggregatedHealthInfo> findBetweenCreated(LocalDate beginDatetime, LocalDate endDatetime);
}
