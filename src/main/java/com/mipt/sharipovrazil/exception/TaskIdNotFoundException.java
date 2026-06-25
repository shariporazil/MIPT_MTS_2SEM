package com.mipt.sharipovrazil.exception;

public class TaskIdNotFoundException extends RuntimeException {
    private final Long missingId;

    public TaskIdNotFoundException(Long missingId) {
        super("Task not found for id: " + missingId);
        this.missingId = missingId;
    }

    public Long getMissingId() {
        return missingId;
    }
}