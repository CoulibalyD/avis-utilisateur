package dracoul.tech.avis.controller;

import dracoul.tech.avis.exception.AvisNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(AvisNotFoundException.class)
    public ResponseEntity<String> handleAvisNotFoundException(AvisNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
