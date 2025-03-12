package com.tasktracker.scheduler.kafka;

import com.tasktracker.scheduler.dto.EmailSendingRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(EmailKafkaProducer.class);

    @Value("${spring.kafka.topic.email-sending}")
    private String topicEmailSending;

    private final KafkaTemplate<String, EmailSendingRequest> kafkaTemplate;

    public void sendTaskReportEmail(EmailSendingRequest request) {
        try {
            kafkaTemplate.send(topicEmailSending, request);
        } catch (Exception e) {
            log.error("Failed to send task report email request to Kafka for: {}", request.to(), e);
            throw new RuntimeException(e);
        }
    }
}
