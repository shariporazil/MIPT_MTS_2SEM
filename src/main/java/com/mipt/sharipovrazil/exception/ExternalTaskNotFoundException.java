package com.mipt.sharipovrazil.exception;

public class ExternalTaskNotFoundException extends RuntimeException {
    public ExternalTaskNotFoundException(Long taskId) {
        super("External task not found: " + taskId);
    }

    public ExternalTaskNotFoundException(String message) {
        super(message);
    }
}