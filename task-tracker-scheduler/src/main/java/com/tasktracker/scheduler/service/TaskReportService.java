package com.tasktracker.scheduler.service;

import java.time.Instant;

public interface TaskReportService {

    void sendTaskReportEmails (Instant start, Instant end);

}
