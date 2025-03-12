package com.tasktracker.emailsender.model;

import java.util.Arrays;

public enum EmailType {
    WELCOME("Welcome to Task Tracker","email/welcome-email"),
    TASK_REPORT("Your daily task report","email/task-report-email"),;

    private final String defaultSubject;
    private final String templateName;

    EmailType(String defaultSubject, String templateName) {
        this.defaultSubject = defaultSubject;
        this.templateName = templateName;
    }

    public String getDefaultSubject() {
        return defaultSubject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public static EmailType fromString(String type) {
        return Arrays.stream(values())
                .filter(typeFromEnum -> typeFromEnum.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown email type: " + type));
    }
}
