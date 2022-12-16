package com.example.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;


@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification implements Serializable {
    public enum Severity {
        MODERATE, SEVERE, CRITICAL
    }

    public enum Status {
        ONGOING, CLOSED
    }

    @Id
    private String id;
    private String description;
    private Severity severity;
    private Status status;
    private LocalDateTime issuedDatetime;
    private LocalDateTime lastProcessedDatetime;
    @Version
    private int version; // Used for data optimistic lock
}
