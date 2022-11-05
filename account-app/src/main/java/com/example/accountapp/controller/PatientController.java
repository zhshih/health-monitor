package com.example.accountapp.controller;

import com.example.accountapp.model.Patient;
import com.example.accountapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Controller
@RequestMapping("/api-v1/patient")
public class PatientController {

    private PatientRepository patientRepository;

    @Autowired
    PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Patient> savePatient(@RequestBody Patient patient) {
        patient.setMonitoredDateTime(LocalDateTime.now());
        return ResponseEntity.ok(patientRepository.save(patient));
    }

    @PutMapping(value="/{patientId}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("patientId") long id, @RequestBody Patient patient) {
        Patient oldPatient = patientRepository.findById(id).orElseThrow();
        oldPatient.setFirstName(patient.getFirstName());
        oldPatient.setLastName(patient.getLastName());
        oldPatient.setHeight(patient.getHeight());
        oldPatient.setWeight(patient.getWeight());
        oldPatient.setBirthDate(patient.getBirthDate());
        oldPatient.setGender(patient.getGender());
        return ResponseEntity.ok(patientRepository.save(oldPatient));
    }

    @PatchMapping(path="/{patientId}")
    public ResponseEntity<Patient> patchOrder(@PathVariable("patientId") long id, @RequestBody Patient patch) {
        Patient oldPatient = patientRepository.findById(id).orElseThrow();
        if (patch.getFirstName() != null) {
            oldPatient.setFirstName(patch.getFirstName());
        }
        if (patch.getLastName() != null) {
            oldPatient.setLastName(patch.getLastName());
        }
        if (patch.getHeight() != 0.0) {
            oldPatient.setHeight(patch.getHeight());
        }
        if (patch.getWeight() != 0.0) {
            oldPatient.setWeight(patch.getWeight());
        }
        if (patch.getBirthDate() != null) {
            oldPatient.setBirthDate(patch.getBirthDate());
        }
        if (patch.getGender() != null) {
            oldPatient.setGender(patch.getGender());
        }
        return ResponseEntity.ok(patientRepository.save(oldPatient));
    }

    @GetMapping(value="/{patientId}")
    ResponseEntity<Patient> getPatient(@PathVariable("patientId") long id) {
        return ResponseEntity.ok(patientRepository.findById(id).orElseThrow());
    }

    @GetMapping
    ResponseEntity<Collection<Patient>> getPatients() {
        List<Patient> patients = new ArrayList<Patient>();
        patientRepository.findAll().forEach(patients::add);
        return ResponseEntity.ok(patients);
    }

    @DeleteMapping(value="/{patientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteSpec(@PathVariable("patientId") long id) {
        patientRepository.deleteById(id);
        return ResponseEntity.ok(String.format("patient with id %s is deleted", id));
    }

}
