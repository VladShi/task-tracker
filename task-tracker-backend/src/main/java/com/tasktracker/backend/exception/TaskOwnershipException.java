package com.tasktracker.backend.exception;

public class TaskOwnershipException extends RuntimeException {
    public TaskOwnershipException() {
        super("Task does not belong to you");
    }
}
