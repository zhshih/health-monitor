package com.example.medicalcare.service;

import com.example.medicalcare.model.Anomaly;
import com.example.medicalcare.model.MedicalInstruction;
import com.example.medicalcare.model.MedicalInstructionMessage;
import com.example.medicalcare.repository.MedicalInstructionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AnomalyService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private MedicalInstructionRepository medicalInstructionRepository;

    public AnomalyService() {
        Runnable runnableTask = () -> {
            log.info("runnable is launched");
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        executorService.execute(runnableTask);
    }

    public void processAnomaly(Anomaly anomaly) {
        if (anomaly.getSeverity() == Anomaly.Severity.MODERATE) {
            if (anomaly.getStatus() == Anomaly.Status.ONGOING) {
                // TODO
//                addModerateQueue(anomaly);
                log.info("add Anomaly={} to moderate Queue", anomaly);
                MedicalInstruction medicalInstruction = getMedicalInstruction(anomaly);
                sendMedicalInstruction(medicalInstruction);
                medicalInstructionRepository.save(medicalInstruction).subscribe();
            }
            else {
//                removedModerateQueue(anomaly);
                log.info("remove Anomaly={} from moderate Queue", anomaly);
            }
        }
        else if (anomaly.getSeverity() == Anomaly.Severity.SEVERE) {
            if (anomaly.getStatus() == Anomaly.Status.ONGOING) {
                // TODO
//                addSevereQueue(anomaly);
                log.info("add Anomaly={} to severe Queue", anomaly);
                MedicalInstruction medicalInstruction = getMedicalInstruction(anomaly);
                sendMedicalInstruction(medicalInstruction);
                medicalInstructionRepository.save(medicalInstruction).subscribe();
            }
            else {
//                removedSevereQueue(anomaly);
                log.info("remove Anomaly={} from severe Queue", anomaly);
            }
        }
        else if (anomaly.getSeverity() == Anomaly.Severity.CRITICAL) {
            if (anomaly.getStatus() == Anomaly.Status.ONGOING) {
                // TODO
//                addCriticalQueue(anomaly);
                log.info("add Anomaly={} to critical Queue", anomaly);
                MedicalInstruction medicalInstruction = getMedicalInstruction(anomaly);
                sendMedicalInstruction(medicalInstruction);
                medicalInstructionRepository.save(medicalInstruction).subscribe();
            }
            else {
//                removedCriticalQueue(anomaly);
                log.info("remove Anomaly={} from critical Queue", anomaly);
            }
        }
    }

    private MedicalInstruction getMedicalInstruction(Anomaly anomaly) {
        MedicalInstruction.MedicalAction action;
        if (anomaly.getSeverity() == Anomaly.Severity.MODERATE) {
            action = MedicalInstruction.MedicalAction.CALL_PATIENT;
        }
        else if (anomaly.getSeverity() == Anomaly.Severity.SEVERE) {
            action = MedicalInstruction.MedicalAction.CALL_NURSE;
        }
        else {
            action = MedicalInstruction.MedicalAction.CALL_DOCTOR_AND_NURSE;
        }
        return MedicalInstruction.builder()
                .id(UUID.randomUUID().toString().replaceAll("-", ""))
                .anomaly(anomaly)
                .medicalAction(action)
                .issuedDatetime(LocalDateTime.now())
                .build();
    }

    private void sendMedicalInstruction(MedicalInstruction medicalInstruction) {
        MedicalInstructionMessage medicalInstructionMessage = MedicalInstructionMessage.from(medicalInstruction);
        log.info("send medicalInstruction = {}", medicalInstructionMessage);
        streamBridge.send("medicalInstruction-out-0", MessageBuilder.withPayload(medicalInstructionMessage).build());
    }
}
