package com.example.healthmonitor.servcie;

import com.example.healthmonitor.model.MedicalInstruction;
import com.example.healthmonitor.repository.MedicalInstructionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MedicalInstructionService {

    @Autowired
    MedicalInstructionRepository medicalInstructionRepository;

    public void process(MedicalInstruction medicalInstruction) {
        log.info("process medicalInstruction {}", medicalInstruction);
        medicalInstructionRepository.save(medicalInstruction).subscribe();
    }
}
