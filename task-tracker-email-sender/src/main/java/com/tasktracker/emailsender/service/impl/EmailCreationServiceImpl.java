package com.tasktracker.emailsender.service.impl;

import com.tasktracker.emailsender.dto.EmailSendingRequest;
import com.tasktracker.emailsender.model.EmailMessage;
import com.tasktracker.emailsender.model.EmailType;
import com.tasktracker.emailsender.service.EmailCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailCreationServiceImpl implements EmailCreationService {

    private final SpringTemplateEngine templateEngine;

    @Autowired
    public EmailCreationServiceImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public EmailMessage create(EmailSendingRequest request) {
        EmailType emailType = EmailType.fromString(request.type());
        Map<String, String> params = request.params();
        String subject = params.getOrDefault("subject", emailType.getDefaultSubject());

        return EmailMessage.builder()
                .to(request.to())
                .subject(subject)
                .htmlContent(renderTemplate(emailType, params))
                .build();
    }

    private String renderTemplate(EmailType emailType, Map<String, String> params) {
        Context context = new Context();
        params.forEach(context::setVariable);
        return templateEngine.process(emailType.getTemplateName(), context);
    }
}
