package com.tasktracker.backend.kafka.listener;

import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.kafka.EmailKafkaProducer;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewUserEventListener {

    private final EmailKafkaProducer emailKafkaProducer;

    @PostPersist
    public void sendEmail(User user) {
        if (user.isNew()) {
            emailKafkaProducer.sendWelcomeEmail(user.getUsername());
            user.setNew(false);
        }
    }
}
