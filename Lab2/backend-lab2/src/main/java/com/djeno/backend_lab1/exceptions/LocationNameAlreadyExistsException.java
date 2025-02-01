package com.djeno.backend_lab1.exceptions;

public class LocationNameAlreadyExistsException extends RuntimeException {

    public LocationNameAlreadyExistsException(String name) {
        super("Location name already exists: " + name);
    }
}
