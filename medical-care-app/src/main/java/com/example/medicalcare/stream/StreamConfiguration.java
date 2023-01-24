package com.example.medicalcare.stream;

import com.example.medicalcare.model.Anomaly;
import com.example.medicalcare.service.AnomalyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Configuration
@Slf4j
public class StreamConfiguration {

    @Autowired
    private AnomalyService anomalyService;
    private static List<Anomaly.Status> statusList = Arrays.asList(Anomaly.Status.ONGOING, Anomaly.Status.CLOSED);
    private static List<Anomaly.Severity> severityList = Arrays.asList(
            Anomaly.Severity.MODERATE, Anomaly.Severity.SEVERE, Anomaly.Severity.CRITICAL);

    @Bean
    public Consumer<Flux<Anomaly>> anomalyConsumer() {
        return flux -> flux
                .doOnNext(anomaly -> log.info("received anomaly = {}", anomaly))
                .doOnNext(anomaly -> anomalyService.processAnomaly(anomaly))
                .subscribe();
    }
}
