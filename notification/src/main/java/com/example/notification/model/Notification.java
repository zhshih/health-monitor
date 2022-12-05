package com.example.notification.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Notification {
    public enum Severity {
        MODERATE, SEVERE, CRITICAL
    }

    public enum Status {
        ONGOING, CLOSED
    }

    private Long id;
    private String name;
    private Severity severity;
    private Status status;
    private LocalDateTime issuedDatetime;
    private LocalDateTime lastProcessedDatetime;
    private String description;
}
