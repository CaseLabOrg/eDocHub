package com.example.ecm.exception;

public class IsDeadException extends RuntimeException {
    public IsDeadException(String message) {
        super(message);
    }
}
