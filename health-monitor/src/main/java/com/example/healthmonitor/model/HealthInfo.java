package com.example.healthmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthInfo {
    private long patientId;
    private double systolicBloodPressure;
    private double diastolicBloodPressure;
    private int heartBeat;
}