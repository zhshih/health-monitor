package com.example.accountapp.model;

import com.example.accountapp.controller.PatientController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatientResourceAssembler extends RepresentationModelAssemblerSupport<Patient, PatientModel> {
    public PatientResourceAssembler() {
        super(PatientController.class, PatientModel.class);
    }

    @Override
    public PatientModel toModel(Patient entity) {
        PatientModel patientModel = createResource(entity);
        patientModel.add(linkTo(
                methodOn(PatientController.class)
                        .getPatient(entity.getId()))
                .withSelfRel());
        return patientModel;
    }

    @Override
    public CollectionModel<PatientModel> toCollectionModel(Iterable<? extends Patient> entities)
    {
        CollectionModel<PatientModel> patientModels = super.toCollectionModel(entities);
        patientModels.add(linkTo(methodOn(PatientController.class).getPatients()).withSelfRel());
        return patientModels;
    }

    private PatientModel createResource(Patient entity) {
        PatientModel patientModel = instantiateModel(entity);
        patientModel.setId(entity.getId());
        patientModel.setFirstName(entity.getFirstName());
        patientModel.setLastName(entity.getLastName());
        patientModel.setHeight(entity.getHeight());
        patientModel.setWeight(entity.getWeight());
        patientModel.setBloodType(entity.getBloodType());
        patientModel.setGender(entity.getGender());
        patientModel.setBirthDate(entity.getBirthDate());
        patientModel.setMonitoredDateTime(entity.getMonitoredDateTime());
        return patientModel;
    }
}
