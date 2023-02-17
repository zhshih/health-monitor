package com.example.healthmonitor.utils;

import com.example.healthmonitor.model.HealthInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class HealthInfoGenerator {

    private static Map<Integer, HealthInfoType> infoTypeMap = new HashMap<>();
    private static HealthInfoType[] healthInfoTypes = HealthInfoType.values();

    public enum HealthInfoType {
        NORMAL, SYSTOLIC_LOW, SYSTOLIC_MODERATE, SYSTOLIC_SEVERE, SYSTOLIC_CRITICAL,
        DIASTOLIC_LOW, DIASTOLIC_MODERATE, DIASTOLIC_SEVERE, DIASTOLIC_CRITICAL,

        // TODO
        // for heartBeat
    }

    public static void generateRandomSeeds() {
        infoTypeMap.clear();
    }

    public static HealthInfoType getRandomHealthInfoType(int id) {
        HealthInfoType infoType = infoTypeMap.get(id);
        if (infoType == null) {
            infoType = healthInfoTypes[
                    ThreadLocalRandom.current().nextInt(0, HealthInfoType.values().length)];
            infoTypeMap.put(id, infoType);
        }
        return infoType;
    }

    public static HealthInfo getRandomInfo(HealthInfoType type, int id) {
        // default Normal info
        HealthInfo healthInfo = HealthInfo.builder()
                .patientId(id)
                .systolicBloodPressure(ThreadLocalRandom.current().nextDouble(91, 120))
                .diastolicBloodPressure(ThreadLocalRandom.current().nextDouble(61, 80))
                .heartBeat(ThreadLocalRandom.current().nextInt(100, 200))
                .createAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        switch (type) {
            case DIASTOLIC_LOW:
            case SYSTOLIC_LOW: {
                healthInfo.setSystolicBloodPressure(ThreadLocalRandom.current().nextDouble(41, 90));
                healthInfo.setDiastolicBloodPressure(ThreadLocalRandom.current().nextDouble(41, 60));
                break;
            }
            case SYSTOLIC_MODERATE: {
                healthInfo.setSystolicBloodPressure(ThreadLocalRandom.current().nextDouble(121, 140));
                break;
            }
            case SYSTOLIC_SEVERE: {
                healthInfo.setSystolicBloodPressure(ThreadLocalRandom.current().nextDouble(141, 160));
                break;
            }
            case SYSTOLIC_CRITICAL: {
                healthInfo.setSystolicBloodPressure(ThreadLocalRandom.current().nextDouble(161, 200));
                break;
            }
            case DIASTOLIC_MODERATE: {
                healthInfo.setDiastolicBloodPressure(ThreadLocalRandom.current().nextDouble(81, 90));
                break;
            }
            case DIASTOLIC_SEVERE: {
                healthInfo.setDiastolicBloodPressure(ThreadLocalRandom.current().nextDouble(91, 100));
                break;
            }
            case DIASTOLIC_CRITICAL: {
                healthInfo.setDiastolicBloodPressure(ThreadLocalRandom.current().nextDouble(101, 140));
                break;
            }
            default:
                break;
        }
        return healthInfo;
    }
}
