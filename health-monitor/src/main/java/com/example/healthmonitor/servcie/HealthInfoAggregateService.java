package com.example.healthmonitor.servcie;

import com.example.healthmonitor.model.AggregatedHealthInfo;
import com.example.healthmonitor.model.HealthInfo;
import com.example.healthmonitor.repository.AggregatedHealthInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class HealthInfoAggregateService {

    private HealthMonitorService healthMonitorService;
    private AggregatedHealthInfoRepository healthInfoRepository;
    private static HashMap<Long, List<HealthInfo>> aggregationMap = new HashMap<>();
    private int AGGREGATE_INTERVAL_SEC = 60;

    public HealthInfoAggregateService (AggregatedHealthInfoRepository healthInfoRepository, HealthMonitorService healthMonitorService) {
        this.healthInfoRepository = healthInfoRepository;
        this.healthMonitorService = healthMonitorService;
    }

    public void putInfo(List<HealthInfo> healthInfos) {
        healthInfos.forEach(
                healthInfo -> {
                    List<HealthInfo> infos = aggregationMap.getOrDefault(healthInfo.getPatientId(), new ArrayList<>());
                    infos.add(healthInfo);
                    aggregationMap.put(healthInfo.getPatientId(), infos);
                }
        );
        int currSecond = LocalDateTime.now().getSecond();
        if (currSecond % AGGREGATE_INTERVAL_SEC == 0) {
            log.info("aggregate info for past {} seconds", AGGREGATE_INTERVAL_SEC);
            List<AggregatedHealthInfo> aggregatedHealthInfos = aggregateInfo(healthInfos);
            healthMonitorService.processHealthInfo(aggregatedHealthInfos);
        }
    }

    private List<AggregatedHealthInfo> aggregateInfo(List<HealthInfo> healthInfos) {
        log.info("aggregationMap = {}", aggregationMap);
        HashMap<Long, AggregatedHealthInfo> aggregatedHealthInfoHashMap = new HashMap<>();
        aggregationMap.forEach(
                (k, v) -> {
                    AggregatedHealthInfo aggregatedHealthInfo = aggregatedHealthInfoHashMap.getOrDefault(k, new AggregatedHealthInfo());
                    aggregatedHealthInfo.setPatientId(k);
                    aggregatedHealthInfo.setAvgSystolicBloodPressure(v.stream()
                            .mapToDouble(o -> o.getSystolicBloodPressure())
                            .average()
                            .orElse(0.0));
                    aggregatedHealthInfo.setMaxSystolicBloodPressure(v.stream()
                            .map(o -> o.getSystolicBloodPressure())
                            .max(Comparator.comparing(Double::intValue))
                            .orElse(0.0));
                    aggregatedHealthInfo.setMinSystolicBloodPressure(v.stream()
                            .map(o -> o.getSystolicBloodPressure())
                            .min(Comparator.comparing(Double::intValue))
                            .orElse(0.0));
                    aggregatedHealthInfo.setAvgDiastolicBloodPressure(v.stream()
                            .mapToDouble(o -> o.getDiastolicBloodPressure())
                            .average()
                            .orElse(0.0));
                    aggregatedHealthInfo.setMaxDiastolicBloodPressure(v.stream()
                            .map(o -> o.getDiastolicBloodPressure())
                            .max(Comparator.comparing(Double::intValue))
                            .orElse(0.0));
                    aggregatedHealthInfo.setMinDiastolicBloodPressure(v.stream()
                            .map(o -> o.getDiastolicBloodPressure())
                            .min(Comparator.comparing(Double::intValue))
                            .orElse(0.0));
                    aggregatedHealthInfo.setAvgHeartBeat(v.stream()
                            .mapToDouble(o -> o.getHeartBeat())
                            .average()
                            .orElse(0.0));
                    aggregatedHealthInfo.setMaxHeartBeat(v.stream()
                            .map(o -> o.getHeartBeat())
                            .max(Comparator.comparing(Integer::intValue))
                            .orElse(0));
                    aggregatedHealthInfo.setMinHeartBeat(v.stream()
                            .map(o -> o.getHeartBeat())
                            .min(Comparator.comparing(Integer::intValue))
                            .orElse(0));
                    aggregatedHealthInfo.setCreateAt(LocalDateTime.now());
                    aggregatedHealthInfoHashMap.put(k, aggregatedHealthInfo);
                }
        );
        List<AggregatedHealthInfo> aggregatedHealthInfos = new ArrayList<>();
        aggregatedHealthInfoHashMap.forEach(
                (k, v) -> {
                    aggregatedHealthInfos.add(v);
                }
        );
        log.info("aggregatedHealthInfos = {}", aggregatedHealthInfos);
        healthInfoRepository.saveAll(aggregatedHealthInfos).subscribe();
        aggregationMap.clear();
        aggregatedHealthInfoHashMap.clear();
        return aggregatedHealthInfos;
    }
}
