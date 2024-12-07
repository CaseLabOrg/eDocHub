package com.example.ecm.exception;

public class AttributeAlreadyExistsException extends RuntimeException {
    public AttributeAlreadyExistsException(String message) {
        super(message);
    }
}
