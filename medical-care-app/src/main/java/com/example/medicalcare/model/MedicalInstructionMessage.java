package com.example.medicalcare.model;

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
public class MedicalInstructionMessage implements Serializable {
    private String id;
    private String anomalyId;
    private MedicalInstruction.MedicalAction medicalAction;
    private LocalDateTime issuedDatetime;

    public static MedicalInstructionMessage from(MedicalInstruction medicalInstruction) {
        return MedicalInstructionMessage.builder()
                .id(medicalInstruction.getId())
                .anomalyId(medicalInstruction.getAnomaly().getId())
                .medicalAction(medicalInstruction.getMedicalAction())
                .issuedDatetime(medicalInstruction.getIssuedDatetime())
                .build();
    }
}
