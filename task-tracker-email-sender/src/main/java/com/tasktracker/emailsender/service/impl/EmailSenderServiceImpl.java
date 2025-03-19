package com.tasktracker.emailsender.service.impl;

import com.tasktracker.emailsender.dto.EmailSendingRequest;
import com.tasktracker.emailsender.model.EmailMessage;
import com.tasktracker.emailsender.service.EmailCreationService;
import com.tasktracker.emailsender.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final EmailCreationService emailCreationService;

    private static final Logger log = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender mailSender,
                              @Value("${spring.mail.verifiedemail}") String fromAddress,
                              EmailCreationService emailCreationService) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.emailCreationService = emailCreationService;
    }

    @Override
    public void send(EmailSendingRequest request) throws Exception {
        EmailMessage email = emailCreationService.create(request);
        log.info("Created email: to={}, subject={}", email.getTo(), email.getSubject());
        MimeMessage mimeMessage = convertToMimeMessage(email);
        mailSender.send(mimeMessage);
        log.info("Email sent successfully to {}", email.getTo());
    }

    private MimeMessage convertToMimeMessage(EmailMessage email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(email.getHtmlContent(), true);
        helper.setFrom(fromAddress);

        return mimeMessage;
    }
}
