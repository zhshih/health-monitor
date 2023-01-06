package com.example.medicalcare.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalInstruction implements Serializable {

    public enum MedicalAction {
        CALL_PATIENT, CALL_NURSE, CALL_DOCTOR_AND_NURSE
    }

    private String id;
    private String anomalyId;
    private MedicalAction medicalAction;
    private LocalDateTime issuedDatetime;
}
