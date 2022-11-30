package com.example.healthmonitor.repository;

import com.example.healthmonitor.model.AggregatedHealthInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedHealthInfoRepository extends ReactiveCrudRepository<AggregatedHealthInfo, String> {
}
