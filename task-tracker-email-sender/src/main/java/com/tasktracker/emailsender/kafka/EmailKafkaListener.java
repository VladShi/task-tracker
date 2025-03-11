package com.tasktracker.emailsender.kafka;

import com.tasktracker.emailsender.dto.EmailSendingRequest;
import com.tasktracker.emailsender.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailKafkaListener {

    private static final Logger log= LoggerFactory.getLogger(EmailKafkaListener.class);
    private final EmailSenderService emailSenderService;

    @Autowired
    public EmailKafkaListener(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.email-sending}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(EmailSendingRequest request) throws Exception {
        log.info("Received Kafka message: {}", request);
        emailSenderService.send(request);
    }

}
