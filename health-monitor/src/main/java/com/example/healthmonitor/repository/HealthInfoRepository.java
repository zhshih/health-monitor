package com.example.healthmonitor.repository;

import com.example.healthmonitor.model.HealthInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthInfoRepository extends ReactiveCrudRepository<HealthInfo, Long> {
}
