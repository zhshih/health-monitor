package com.example.healthmonitor.repository;

import com.example.healthmonitor.model.MedicalInstruction;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MedicalInstructionRepoImpl implements MedicalInstructionRepository {

    private final static String KEY = "MedicalInstruction";

    private final ReactiveRedisOperations<String, MedicalInstruction> redisOperations;
    private final ReactiveHashOperations<String, String, MedicalInstruction> hashOperations;

    @Autowired
    public MedicalInstructionRepoImpl(ReactiveRedisOperations<String, MedicalInstruction> redisOperations) {
        this.redisOperations = redisOperations;
        this.hashOperations = redisOperations.opsForHash();
    }

    @Override
    public Mono<MedicalInstruction> findByAnomalyId(String anomalyId) {
        return hashOperations.values(KEY)
                .filter(u -> u.getAnomalyId().equals(anomalyId))
                .singleOrEmpty();
    }

    @Override
    public <S extends MedicalInstruction> Mono<S> save(S entity) {
        if(entity.getAnomalyId().isEmpty())
            return Mono.error(new IllegalArgumentException("cannot be saved: anomalyId is required"))
                    .thenReturn(entity);

        return hashOperations.put(KEY, entity.getId(), entity)
                .map(isSaved -> entity);
    }

    @Override
    public <S extends MedicalInstruction> Flux<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends MedicalInstruction> Flux<S> saveAll(Publisher<S> entityStream) {
        return null;
    }

    @Override
    public Mono<MedicalInstruction> findById(String id) {
        return hashOperations.get(KEY, id);
    }

    @Override
    public Mono<MedicalInstruction> findById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return hashOperations.hasKey(KEY, id);
    }

    @Override
    public Mono<Boolean> existsById(Publisher<String> id) {
        return null;
    }

    @Override
    public Flux<MedicalInstruction> findAll() {
        return hashOperations.values(KEY);
    }

    @Override
    public Flux<MedicalInstruction> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public Flux<MedicalInstruction> findAllById(Publisher<String> idStream) {
        return null;
    }

    @Override
    public Mono<Long> count() {
        return hashOperations.values(KEY).count();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return hashOperations.remove(KEY, id).then();
    }

    @Override
    public Mono<Void> deleteById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Void> delete(MedicalInstruction entity) {
        return hashOperations.remove(KEY, entity.getId()).then();
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<? extends String> strings) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends MedicalInstruction> entities) {
        return hashOperations.delete(KEY).then();
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends MedicalInstruction> entityStream) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return hashOperations.delete(KEY).then();
    }
}
