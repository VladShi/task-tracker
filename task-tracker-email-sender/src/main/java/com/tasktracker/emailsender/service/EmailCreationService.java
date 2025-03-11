package com.tasktracker.emailsender.service;

import com.tasktracker.emailsender.dto.EmailSendingRequest;
import com.tasktracker.emailsender.model.EmailMessage;
import com.tasktracker.emailsender.model.EmailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailCreationService {

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailCreationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public EmailMessage create(EmailSendingRequest request) {
        EmailType emailType = EmailType.fromType(request.type());
        Map<String, String> params = request.params();

        return EmailMessage.builder()
                .to(request.to())
                .subject(getSubject(emailType, params))
                .htmlContent(renderTemplate(emailType, params))
                .build();
    }

    private String renderTemplate(EmailType emailType, Map<String, String> params) {
        Context context = new Context();
        params.forEach(context::setVariable);
        return templateEngine.process(emailType.getTemplateName(), context);
    }

    private String getSubject(EmailType emailType, Map<String, String> params) {
        return params.getOrDefault("subject", emailType.getDefaultSubject());
    }
}
