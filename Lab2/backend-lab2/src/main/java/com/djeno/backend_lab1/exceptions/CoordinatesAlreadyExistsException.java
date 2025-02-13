package com.djeno.backend_lab1.exceptions;

public class CoordinatesAlreadyExistsException extends RuntimeException {
    public CoordinatesAlreadyExistsException(String message) {
        super(message);
    }
}
