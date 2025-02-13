package com.djeno.backend_lab1.exceptions;

public class StudyGroupAlreadyExistsException extends RuntimeException {
    public StudyGroupAlreadyExistsException(String message) {
        super(message);
    }
}
