package com.tasktracker.backend.kafka;

import com.tasktracker.backend.dto.request.EmailSendingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailKafkaProducer {

    @Value("${spring.kafka.topic.email-sending}")
    private String topicEmailSending;

    private static final String WELCOME_EMAIL_TYPE = "WELCOME";

    private final KafkaTemplate<String, EmailSendingRequest> kafkaTemplate;

    public void sendWelcomeEmail(String to) {
        EmailSendingRequest request = new EmailSendingRequest(
                to,
                WELCOME_EMAIL_TYPE,
                new HashMap<>()
        );

        CompletableFuture.supplyAsync(() -> kafkaTemplate.send(topicEmailSending, request).join())
                .orTimeout(10, TimeUnit.SECONDS)
                .handle((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send welcome email request to Kafka for: {}", to, ex.getCause());
                    } else {
                        log.info("Sent welcome email request to Kafka: {}", request);
                    }
                    return null;
                });
    }
}
