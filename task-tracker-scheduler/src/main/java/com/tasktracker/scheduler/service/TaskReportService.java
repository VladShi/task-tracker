package com.tasktracker.scheduler.service;

import java.time.Instant;

public interface TaskReportService {

    void sendEmailsForPeriod(Instant start, Instant end);

}
