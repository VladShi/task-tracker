package com.tasktracker.scheduler.service.impl;

import com.tasktracker.scheduler.dto.EmailSendingRequest;
import com.tasktracker.scheduler.entity.User;
import com.tasktracker.scheduler.kafka.EmailKafkaProducer;
import com.tasktracker.scheduler.report.ReportGenerator;
import com.tasktracker.scheduler.repository.UserRepository;
import com.tasktracker.scheduler.service.EmailSendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailSendingServiceImpl implements EmailSendingService {

    private static final int PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final EmailKafkaProducer emailKafkaProducer;

    @Override
    public void sendEmailsForPeriod(Instant start, Instant end, ReportGenerator reportGenerator) {

        int pageNumber = 0;
        Slice<User> usersReceivingTaskReportSlice;

        do {
            Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
            usersReceivingTaskReportSlice = userRepository.findUsersWithUncompletedOrCompletedInPeriodTasks(
                    start, end, pageable);

            for (User user : usersReceivingTaskReportSlice) {
                EmailSendingRequest request = reportGenerator.generateForUserInPeriod(user, start, end);
                emailKafkaProducer.sendTaskReportEmail(request);
            }
            pageNumber++;
        } while (usersReceivingTaskReportSlice.hasNext());
    }
}
