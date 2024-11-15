package com.djeno.backend_lab1.exceptions;

public class InvalidSignRequestException extends RuntimeException {
    public InvalidSignRequestException(String message) {
        super(message);
    }
}
