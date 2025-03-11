package com.tasktracker.emailsender.model;

public class EmailMessage {
    private final String to;
    private final String subject;
    private final String htmlContent;

    private EmailMessage(String to, String subject, String htmlContent) {
        this.to = to;
        this.subject = subject;
        this.htmlContent = htmlContent;
    }

    public static EmailMessageBuilder builder() {
        return new EmailMessageBuilder();
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public static class EmailMessageBuilder {
        private String to;
        private String subject;
        private String htmlContent;

        public EmailMessageBuilder to(String to) {
            this.to = to;
            return this;
        }

        public EmailMessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailMessageBuilder htmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }

        public EmailMessage build() {
            return new EmailMessage(to, subject, htmlContent);
        }
    }
}