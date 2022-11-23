package com.example.healthmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class HealthInfo {
    @Id
    private String id;
    private long patientId;
    private double systolicBloodPressure;
    private double diastolicBloodPressure;
    private int heartBeat;
    private LocalDateTime createAt;
}