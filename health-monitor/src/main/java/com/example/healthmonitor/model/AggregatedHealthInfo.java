package com.example.healthmonitor.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document
@Data
public class AggregatedHealthInfo {
    @Id
    private String id;
    @NotNull
    private long patientId;
    private double avgSystolicBloodPressure;
    private double minSystolicBloodPressure;
    private double maxSystolicBloodPressure;
    private double avgDiastolicBloodPressure;
    private double minDiastolicBloodPressure;
    private double maxDiastolicBloodPressure;
    private double avgHeartBeat;
    private double minHeartBeat;
    private double maxHeartBeat;
    private LocalDateTime createAt;
}
