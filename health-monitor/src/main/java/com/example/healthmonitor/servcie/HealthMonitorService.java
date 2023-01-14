package com.example.healthmonitor.servcie;

import com.example.healthmonitor.model.AggregatedHealthInfo;
import com.example.healthmonitor.model.Anomaly;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.example.healthmonitor.servcie.HealthMonitorService.BloodPressureStatus.*;

@Service
@Slf4j
public class HealthMonitorService {

    public enum BloodPressureStatus {
        NORMAL, LOW_BLOOD_PRESSURE, PRE_HYPERTENSION, STAGE1_HYPERTENSION, STAGE2_HYPERTENSION
    }

    @Autowired
    private StreamBridge streamBridge;
    private static HashMap<Long, Integer> lowBloodPressureWatchedMap = new HashMap<>();
    private static HashMap<Long, Integer> preHypertensionWatchedMap = new HashMap<>();
    private static HashMap<Long, Integer> stage1HypertensionWatchedMap = new HashMap<>();
    private static HashMap<Long, Integer> stage2HypertensionWatchedMap = new HashMap<>();
    private static HashMap<Long, Pair<BloodPressureStatus, Integer>> bloodPressureWatchedMap = new HashMap<>();

    public void processHealthInfo(List<AggregatedHealthInfo> aggregatedHealthInfos) {
        aggregatedHealthInfos.forEach(aggregatedHealthInfo -> {
            processInfo(aggregatedHealthInfo);
        });
        log.info("process healthInfo = " + aggregatedHealthInfos);
        processAnomaly();
    }

    private void processAnomaly() {
        bloodPressureWatchedMap.forEach(
                (id, info) -> {
                    int counter = info.getRight();
                    BloodPressureStatus bloodPressureStatus = info.getLeft();
                    if (counter == 1) {
                        log.info("LEVEL 0 Anomaly of {} where patientId = {}", bloodPressureStatus, id);
                    }
                    else if (counter >= 5) {
                        log.info("LEVEL 5 Anomaly of {} where patientId = {}", bloodPressureStatus, id);
                        Anomaly.Severity severity;
                        if (bloodPressureStatus == PRE_HYPERTENSION) {
                            severity = Anomaly.Severity.SEVERE;
                        }
                        else if (bloodPressureStatus == STAGE1_HYPERTENSION ||
                                bloodPressureStatus == STAGE2_HYPERTENSION) {
                            severity = Anomaly.Severity.CRITICAL;
                        }
                        else {
                            severity = Anomaly.Severity.MODERATE;
                        }

                        Anomaly anomaly = Anomaly.builder()
                                .id(UUID.randomUUID().toString())
                                .patientId(id)
                                .type(Anomaly.AnomalyType.BLOOD_PRESSURE)
                                .status(Anomaly.Status.ONGOING)
                                .severity(severity)
                                .description("this is Anomaly of " + info.getLeft())
                                .issuedDatetime(LocalDateTime.now())
                                .build();
                        log.info("send anomaly = {}", anomaly);
                        streamBridge.send("anomaly-out-0", MessageBuilder.withPayload(anomaly).build());
                    }
                }
        );
    }

    private void processInfo(AggregatedHealthInfo aggregatedHealthInfo) {
        double avgSystolicBloodPressure = aggregatedHealthInfo.getAvgSystolicBloodPressure(),
                avgAvgDiastolicBloodPressure = aggregatedHealthInfo.getAvgDiastolicBloodPressure();
        if (avgSystolicBloodPressure < 90 && avgAvgDiastolicBloodPressure < 60) {
            log.info("observation of {} is triggered where patientId = {}," +
                            "avgSystolicBloodPressure = {} and avgAvgDiastolicBloodPressure = {}",
                    LOW_BLOOD_PRESSURE, aggregatedHealthInfo.getPatientId(),
                    avgSystolicBloodPressure, avgAvgDiastolicBloodPressure);
            Pair<BloodPressureStatus, Integer> watchedInfo = bloodPressureWatchedMap.getOrDefault(
                    aggregatedHealthInfo.getPatientId(), Pair.of(LOW_BLOOD_PRESSURE, 0));
            if (watchedInfo.getLeft() == LOW_BLOOD_PRESSURE) {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(watchedInfo.getLeft(), watchedInfo.getRight() + 1));
            }
            else {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(LOW_BLOOD_PRESSURE, 1));
            }
        }
        else if ((avgSystolicBloodPressure >= 90 && avgSystolicBloodPressure < 120) &&
                (avgAvgDiastolicBloodPressure >= 60 && avgAvgDiastolicBloodPressure < 80)) {
            Pair<BloodPressureStatus, Integer> watchedInfo = bloodPressureWatchedMap.getOrDefault(
                    aggregatedHealthInfo.getPatientId(), Pair.of(NORMAL, 0));
        }
        else if ((avgSystolicBloodPressure >= 120 && avgSystolicBloodPressure < 140) ||
                (avgAvgDiastolicBloodPressure >= 80 && avgAvgDiastolicBloodPressure < 90)) {
            log.info("observation of {} is triggered where patientId = {}," +
                            "avgSystolicBloodPressure = {} and avgAvgDiastolicBloodPressure = {}",
                    PRE_HYPERTENSION, aggregatedHealthInfo.getPatientId(),
                    avgSystolicBloodPressure, avgAvgDiastolicBloodPressure);
            Pair<BloodPressureStatus, Integer> watchedInfo = bloodPressureWatchedMap.getOrDefault(
                    aggregatedHealthInfo.getPatientId(), Pair.of(PRE_HYPERTENSION, 0));
            if (watchedInfo.getLeft() == PRE_HYPERTENSION) {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(watchedInfo.getLeft(), watchedInfo.getRight() + 1));
            }
            else {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(PRE_HYPERTENSION, 1));
            }
        }
        else if ((avgSystolicBloodPressure >= 140 && avgSystolicBloodPressure < 160) ||
                (avgAvgDiastolicBloodPressure >= 90 && avgAvgDiastolicBloodPressure < 100)) {
            log.info("observation of {} is triggered where patientId = {}," +
                            "avgSystolicBloodPressure = {} and avgAvgDiastolicBloodPressure = {}",
                    STAGE1_HYPERTENSION, aggregatedHealthInfo.getPatientId(),
                    avgSystolicBloodPressure, avgAvgDiastolicBloodPressure);
            Pair<BloodPressureStatus, Integer> watchedInfo = bloodPressureWatchedMap.getOrDefault(
                    aggregatedHealthInfo.getPatientId(), Pair.of(STAGE1_HYPERTENSION, 0));
            if (watchedInfo.getLeft() == STAGE1_HYPERTENSION) {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(watchedInfo.getLeft(), watchedInfo.getRight() + 1));
            }
            else {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(STAGE1_HYPERTENSION, 1));
            }
        }
        else if (avgSystolicBloodPressure >= 160 || avgAvgDiastolicBloodPressure >= 100) {
            log.info("observation of {} is triggered where patientId = {}," +
                            "avgSystolicBloodPressure = {} and avgAvgDiastolicBloodPressure = {}",
                    STAGE2_HYPERTENSION, aggregatedHealthInfo.getPatientId(),
                    avgSystolicBloodPressure, avgAvgDiastolicBloodPressure);
            Pair<BloodPressureStatus, Integer> watchedInfo = bloodPressureWatchedMap.getOrDefault(
                    aggregatedHealthInfo.getPatientId(), Pair.of(STAGE2_HYPERTENSION, 0));
            if (watchedInfo.getLeft() == STAGE2_HYPERTENSION) {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(watchedInfo.getLeft(), watchedInfo.getRight() + 1));
            }
            else {
                bloodPressureWatchedMap.put(aggregatedHealthInfo.getPatientId(),
                        Pair.of(STAGE2_HYPERTENSION, 1));
            }
        }
        else  {
            log.warn("unexpected observation where patientId = {}, avgSystolicBloodPressure = {} " +
                            "and avgAvgDiastolicBloodPressure = {}", aggregatedHealthInfo.getPatientId(),
                    avgSystolicBloodPressure, avgAvgDiastolicBloodPressure);
        }
    }
}
