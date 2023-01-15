package com.example.healthmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Anomaly implements Serializable {
    public enum Severity {
        MODERATE, SEVERE, CRITICAL
    }

    public enum Status {
        ONGOING, CLOSED
    }

    public enum AnomalyType {
        BLOOD_PRESSURE, HEART_BEAT
    }

    private String id;
    @NotNull
    private long patientId;
    private String description;
    private Severity severity;
    private Status status;
    private AnomalyType type;
    private LocalDateTime issuedDatetime;
    private LocalDateTime lastProcessedDatetime;
}
