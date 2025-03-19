package com.tasktracker.scheduler.service;

import com.tasktracker.scheduler.report.ReportGenerator;

import java.time.Instant;

public interface EmailSendingService {

    void sendEmailsForPeriod(Instant start, Instant end, ReportGenerator generator);

}
