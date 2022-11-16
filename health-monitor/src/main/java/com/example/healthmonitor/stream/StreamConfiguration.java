package com.example.healthmonitor.stream;

import com.example.healthmonitor.model.HealthInfo;
import com.example.healthmonitor.servcie.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class StreamConfiguration {

    @Autowired
    private TestService testService;

    @Bean
    public Supplier<Flux<List<HealthInfo>>> healthInfoSupplier() {
        // simulate the source of health info aggregated from monitored patients of every second
        return () -> Flux.fromStream(Stream.generate(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // ignore
            }
            List<HealthInfo> healthInfos = new ArrayList<>();
            for (int i = 10001; i <= 10005; i++) {
                HealthInfo healthInfo = HealthInfo.builder()
                        .patientId(i)
                        .systolicBloodPressure(ThreadLocalRandom.current().nextDouble(100, 200))
                        .diastolicBloodPressure(ThreadLocalRandom.current().nextDouble(100, 200))
                        .heartBeat(ThreadLocalRandom.current().nextInt(100, 200))
                        .build();
                healthInfos.add(healthInfo);
            }
            return healthInfos;
        })).subscribeOn(Schedulers.boundedElastic()).share();
    }

    @Bean
    public Consumer<Flux<List<HealthInfo>>> healthInfoConsumer() {
        return flux -> flux
                .doOnNext(healthInfos -> log.info("received healthInfo = {}", healthInfos))
                .doOnNext(healthInfos -> testService.processHealthInfo(healthInfos))
                .subscribe();
    }
}
