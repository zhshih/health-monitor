package com.example.healthmonitor.servcie;

import com.example.healthmonitor.model.HealthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TestService {
    public void processHealthInfo(List<HealthInfo> healthInfos) {
        log.info("process healthInfo = " + healthInfos);
    }
}
