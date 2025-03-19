package com.tasktracker.scheduler.report;

import com.tasktracker.scheduler.dto.EmailSendingRequest;
import com.tasktracker.scheduler.entity.User;

import java.time.Instant;

public interface ReportGenerator {

    EmailSendingRequest generateForUserInPeriod(User user, Instant start, Instant end);

}
