package com.tasktracker.scheduler.service;

import com.tasktracker.scheduler.dto.EmailSendingRequest;
import com.tasktracker.scheduler.entity.User;
import com.tasktracker.scheduler.kafka.EmailKafkaProducer;
import com.tasktracker.scheduler.report.TaskReportGenerator;
import com.tasktracker.scheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TaskReportService {

    private static final int PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final EmailKafkaProducer emailKafkaProducer;

    public void sendTaskReportEmails (Instant start, Instant end) {

        int pageNumber = 0;
        Slice<User> usersReceivingTaskReportSlice;

        do {
            Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
            usersReceivingTaskReportSlice = userRepository.findUsersWithUncompletedOrCompletedInPeriodTasks(
                    start, end, pageable);

            for (User user : usersReceivingTaskReportSlice) {
                EmailSendingRequest request = TaskReportGenerator.generateForUserInPeriod(user, start, end);
                emailKafkaProducer.sendTaskReportEmail(request);
            }
        } while (usersReceivingTaskReportSlice.hasNext());
    }
}
