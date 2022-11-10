package com.example.accountapp.controller;

import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@ControllerAdvice(annotations = RestController.class)
public class PatientControllerAdvice {

    private final MediaType vndErrorMediaType = MediaType.parseMediaType("application/vnd.error");

    @ExceptionHandler(Exception.class)
    ResponseEntity<Problem> notFoundException(Exception e) {
        return this.error(e, HttpStatus.NOT_FOUND);
    }

    private <E extends Exception> ResponseEntity<Problem> error(E error, HttpStatus httpStatus) {
        String msg = Optional.of(error.getMessage()).orElse(
                error.getClass().getSimpleName());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(this.vndErrorMediaType);
        return new ResponseEntity<>(Problem.create()
                .withDetail(error.getMessage()), httpHeaders, httpStatus);
    }
}
