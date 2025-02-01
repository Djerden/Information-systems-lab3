package com.djeno.backend_lab1.exceptions;

public class StudyGroupNameAlreadyExistsException extends RuntimeException {
    public StudyGroupNameAlreadyExistsException(String message) {
        super(message);
    }
}
