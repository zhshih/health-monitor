package com.example.healthmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private MedicalAction medicalAction;
    private LocalDateTime issuedDatetime;
}
