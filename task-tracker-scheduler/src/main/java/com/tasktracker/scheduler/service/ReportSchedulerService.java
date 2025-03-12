package com.tasktracker.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReportSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(ReportSchedulerService.class);
    private final TaskReportService taskReportService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyReports() {
        log.info("Starting daily report generation");
        Instant now = Instant.now();
        Instant startOfYesterday = now.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant endOfYesterday = startOfYesterday.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        taskReportService.sendTaskReportEmails(startOfYesterday, endOfYesterday);
        log.info("Finished daily report generation");
    }
}
