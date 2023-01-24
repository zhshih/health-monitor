package com.example.medicalcare.repository;

import com.example.medicalcare.model.Anomaly;
import com.example.medicalcare.model.MedicalInstruction;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface MedicalInstructionRepository extends ReactiveCrudRepository<MedicalInstruction, String> {

    @Query(value="{'issuedDatetime': {$gt: ?0, $lt: ?1}}", sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByCriteria(LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.severity': ?0, 'issuedDatetime': {$gt: ?1, $lt: ?2}}", sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByCriteria(Anomaly.Severity severity, LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.status': ?0, 'issuedDatetime': {$gt: ?1, $lt: ?2}}", sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByCriteria(Anomaly.Status status, LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.severity': ?0, 'anomaly.status': ?1, 'issuedDatetime': {$gt: ?2, $lt: ?3}}",
            sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByCriteria(Anomaly.Severity severity, Anomaly.Status status,
                                            LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.patientId': ?0, 'anomaly.severity': ?1, 'anomaly.status': ?2," +
            "'issuedDatetime': {$gt: ?3, $lt: ?4}}", sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByPatientIdAndCriteria(Long id, Anomaly.Severity severity, Anomaly.Status status,
                                             LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.patientId': ?0, 'anomaly.severity': ?1, 'issuedDatetime': {$gt: ?2, $lt: ?3}}",
            sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByPatientIdAndCriteria(Long id, Anomaly.Severity severity,
                                                        LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.patientId': ?0, 'anomaly.status': ?1, 'issuedDatetime': {$gt: ?2, $lt: ?3}}",
            sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByPatientIdAndCriteria(Long id, Anomaly.Status status,
                                                        LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly.patientId': ?0, 'issuedDatetime': {$gt: ?1, $lt: ?2}}", sort="{'issuedDatetime': 1}")
    Flux<MedicalInstruction> findByPatientIdAndCriteria(Long id, LocalDate beginDate, LocalDate endDate);

    @Query(value="{'anomaly._id': ?0}")
    Mono<MedicalInstruction> findByAnomalyId(String id);
}
