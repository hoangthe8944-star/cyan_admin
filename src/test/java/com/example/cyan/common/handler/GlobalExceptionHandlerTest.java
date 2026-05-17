package com.example.cyan.common.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsDuplicateKeyExceptionToConflict() {
        ResponseEntity<Map<String, Object>> response = handler
                .handleDataIntegrity(new DuplicateKeyException("E11000 duplicate key error"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate unique field value", response.getBody().get("error"));
    }

    @Test
    void mapsDataIntegrityViolationToConflict() {
        ResponseEntity<Map<String, Object>> response = handler
                .handleDataIntegrity(new DataIntegrityViolationException("E11000 duplicate key error"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate unique field value", response.getBody().get("error"));
    }

    @Test
    void usesGenericConstraintMessageForOtherIntegrityErrors() {
        ResponseEntity<Map<String, Object>> response = handler
                .handleDataIntegrity(new DataIntegrityViolationException("write failed"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Database constraint violation", response.getBody().get("error"));
    }
}
