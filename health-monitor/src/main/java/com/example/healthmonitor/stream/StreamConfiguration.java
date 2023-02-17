package com.example.healthmonitor.stream;

import com.example.healthmonitor.model.HealthInfo;
import com.example.healthmonitor.model.MedicalInstruction;
import com.example.healthmonitor.servcie.HealthInfoAggregateService;
import com.example.healthmonitor.servcie.MedicalInstructionService;
import com.example.healthmonitor.utils.HealthInfoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class StreamConfiguration {

    private static int INIT_PATIENT_ID = 1;
    private static int MAX_PATIENT_NUM = 5;

    @Autowired
    private HealthInfoAggregateService healthInfoAggregatorService;
    @Autowired
    private MedicalInstructionService medicalInstructionService;
    private LocalDateTime prev;

    @Bean
    public Supplier<Flux<List<HealthInfo>>> healthInfoSupplier() {
        // simulate the source of health info aggregated from monitored patients of every second
        return () -> Flux.fromStream(Stream.generate(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // ignore
            }
            LocalDateTime curr = LocalDateTime.now();
            List<HealthInfo> healthInfoList = new ArrayList<>();
            for (int i = INIT_PATIENT_ID; i <= MAX_PATIENT_NUM; i++) {
                HealthInfoGenerator.HealthInfoType infoType = HealthInfoGenerator.getRandomHealthInfoType(i);
                if (prev == null || Duration.between(prev, curr).toSeconds() >= 300) {
                    if (i == INIT_PATIENT_ID) HealthInfoGenerator.generateRandomSeeds();
                    infoType = HealthInfoGenerator.getRandomHealthInfoType(i);
                    log.info("infoType = {}, i = {}", infoType, i);
                    if (i == MAX_PATIENT_NUM) prev = LocalDateTime.now();
                }
                HealthInfo healthInfo = HealthInfoGenerator.getRandomInfo(infoType, i);
                healthInfoList.add(healthInfo);
            }
            return healthInfoList;
        })).subscribeOn(Schedulers.boundedElastic()).share();
    }

    @Bean
    public Consumer<Flux<List<HealthInfo>>> healthInfoConsumer() {
        return flux -> flux
//                .doOnNext(healthInfos -> log.info("received healthInfo = {}", healthInfos))
                .doOnNext(healthInfos -> healthInfoAggregatorService.putInfo(healthInfos))
                .subscribe();
    }

    @Bean
    public Consumer<Flux<MedicalInstruction>> medicalInstructionConsumer() {
        return flux -> flux
                .doOnNext(medicalInstruction -> log.info("received medicalInstruction = {}", medicalInstruction))
                .doOnNext(medicalInstruction -> medicalInstructionService.process(medicalInstruction))
                .subscribe();
    }
}
