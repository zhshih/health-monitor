package com.example.medicalcare.service;

import com.example.medicalcare.model.Anomaly;
import com.example.medicalcare.model.MedicalInstruction;
import com.example.medicalcare.model.MedicalInstructionMessage;
import com.example.medicalcare.repository.MedicalInstructionRepository;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private HashedWheelTimer hashedWheelTimer;

    public AnomalyService() {
        Runnable runnableTask = () -> {
            log.info("runnable is launched");
            hashedWheelTimer = new HashedWheelTimer(1000,TimeUnit.MILLISECONDS, 64);
            log.info("timer is submitted");
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
                hashedWheelTimer.newTimeout((t) -> {
                    log.info("send alarm to end user where the {Severity, Status} = {{}, {}}",
                            anomaly.getSeverity(), anomaly.getStatus());
                }, 30, TimeUnit.MINUTES);
                MedicalInstruction medicalInstruction = getMedicalInstruction(anomaly);
                sendMedicalInstruction(medicalInstruction);
                medicalInstructionRepository.save(medicalInstruction).subscribe();
            }
        }
        else if (anomaly.getSeverity() == Anomaly.Severity.SEVERE) {
            if (anomaly.getStatus() == Anomaly.Status.ONGOING) {
                hashedWheelTimer.newTimeout((t) -> {
                    log.info("send alarm to end user where the {Severity, Status} = {{}, {}}",
                            anomaly.getSeverity(), anomaly.getStatus());
                }, 15, TimeUnit.MINUTES);
                MedicalInstruction medicalInstruction = getMedicalInstruction(anomaly);
                sendMedicalInstruction(medicalInstruction);
                medicalInstructionRepository.save(medicalInstruction).subscribe();
            }
        }
        else if (anomaly.getSeverity() == Anomaly.Severity.CRITICAL) {
            if (anomaly.getStatus() == Anomaly.Status.ONGOING) {
                hashedWheelTimer.newTimeout((t) -> {
                    log.info("send alarm to end user where the {Severity, Status} = {{}, {}}",
                            anomaly.getSeverity(), anomaly.getStatus());
                }, 1, TimeUnit.MINUTES);
                log.info("add Anomaly={} to critical Queue", anomaly);
                MedicalInstruction medicalInstruction = getMedicalInstruction(anomaly);
                sendMedicalInstruction(medicalInstruction);
                medicalInstructionRepository.save(medicalInstruction).subscribe();
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
