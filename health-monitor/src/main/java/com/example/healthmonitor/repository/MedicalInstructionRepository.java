package com.example.healthmonitor.repository;

import com.example.healthmonitor.model.MedicalInstruction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MedicalInstructionRepository extends ReactiveCrudRepository<MedicalInstruction, String> {
    Mono<MedicalInstruction> findByAnomalyId(String anomalyId);
}
