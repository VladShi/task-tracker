package com.tasktracker.emailsender.model;

import java.util.Arrays;

public enum EmailType {
    WELCOME("WELCOME", "Welcome to Task Tracker","email/welcome-email");

    private final String type;
    private final String defaultSubject;
    private final String templateName;

    EmailType(String type, String defaultSubject, String templateName) {
        this.type = type;
        this.defaultSubject = defaultSubject;
        this.templateName = templateName;
    }

    public String getDefaultSubject() {
        return defaultSubject;
    }

    public String getType() {
        return type;
    }

    public String getTemplateName() {
        return templateName;
    }

    public static EmailType fromType(String type) {
        return Arrays.stream(values())
                .filter(e -> e.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown email type: " + type));
    }
}
