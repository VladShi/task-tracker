package com.tasktracker.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportSchedulerService {

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
