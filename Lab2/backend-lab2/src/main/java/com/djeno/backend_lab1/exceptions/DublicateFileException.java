package com.djeno.backend_lab1.exceptions;

public class DublicateFileException extends RuntimeException {
    public DublicateFileException(String message) {
        super(message);
    }
}
