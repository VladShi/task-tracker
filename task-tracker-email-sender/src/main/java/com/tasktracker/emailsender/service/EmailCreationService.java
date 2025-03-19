package com.tasktracker.emailsender.service;

import com.tasktracker.emailsender.dto.EmailSendingRequest;
import com.tasktracker.emailsender.model.EmailMessage;

public interface EmailCreationService {

    EmailMessage create(EmailSendingRequest request);

}
