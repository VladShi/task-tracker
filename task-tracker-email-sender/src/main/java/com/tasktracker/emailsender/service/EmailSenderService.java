package com.tasktracker.emailsender.service;

import com.tasktracker.emailsender.dto.EmailSendingRequest;

public interface EmailSenderService {

    void send(EmailSendingRequest request) throws Exception;

}
