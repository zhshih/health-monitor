package com.example.healthmonitor.servcie;

import com.example.healthmonitor.model.HealthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.healthmonitor.repository.HealthInfoRepository;

import java.util.List;

@Service
@Slf4j
public class HealthMonitorService {

    private HealthInfoRepository healthInfoRepository;

    public HealthMonitorService (HealthInfoRepository healthInfoRepository) {
        this.healthInfoRepository = healthInfoRepository;
    }

    public void processHealthInfo(List<HealthInfo> healthInfos) {
        healthInfoRepository.saveAll(healthInfos).subscribe();
        healthInfos.forEach(healthInfo -> {
            processRealtimeInfo(healthInfo);
        });
        log.info("process healthInfo = " + healthInfos);
    }

    private void processRealtimeInfo(HealthInfo healthInfo) {
        // TODO: add the logic to deal with real time health Info
    }
}
