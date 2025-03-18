package com.tasktracker.backend.exception;

public class TaskDeletingException extends RuntimeException {
    public TaskDeletingException() {
        super("Task deleting failed. Task doesn't exist or doesn't belong to the current user.");
    }
}
