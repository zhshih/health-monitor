package com.example.medicalcare.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @Id
    private String id;
    private Long patientId;
    private String description;
    private Severity severity;
    private Status status;
    private AnomalyType type;
    private MedicalInstruction medicalInstruction;
    private LocalDateTime issuedDatetime;
    private LocalDateTime lastProcessedDatetime;
    @Version
    private int version; // Used for data optimistic lock
}
