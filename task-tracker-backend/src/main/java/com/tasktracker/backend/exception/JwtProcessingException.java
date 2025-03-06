package com.tasktracker.backend.exception;

public class JwtProcessingException extends RuntimeException {
    public JwtProcessingException(String message) {
        super(message);
    }
}
