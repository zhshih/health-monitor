package com.example.accountapp.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class Patient {

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum BloodType {
        A, B, AB, O
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private double height;
    private double weight;
    private BloodType bloodType;
    private String birthDate;
    private Gender gender;
    private LocalDateTime monitoredDateTime;
}
