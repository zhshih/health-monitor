package com.example.accountapp.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "patients")
public class PatientModel extends RepresentationModel<PatientModel> {
    private long id;
    private String firstName;
    private String lastName;
    private double height;
    private double weight;
    private Patient.BloodType bloodType;
    private String birthDate;
    private Patient.Gender gender;
    private LocalDateTime monitoredDateTime;
}
