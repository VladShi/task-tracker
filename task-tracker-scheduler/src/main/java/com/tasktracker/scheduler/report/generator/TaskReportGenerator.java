package com.tasktracker.scheduler.report.generator;

import com.tasktracker.scheduler.dto.EmailSendingRequest;
import com.tasktracker.scheduler.entity.User;
import com.tasktracker.scheduler.entity.UserTask;
import com.tasktracker.scheduler.report.ReportGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskReportGenerator implements ReportGenerator {

    private static final int TASK_LIMIT = 5;
    private static final String TASK_REPORT_TYPE = "TASK_REPORT";

    private static final String SUBJECT_ALL_TASKS = "Yesterday you completed %d tasks, with %d tasks remaining unfinished";
    private static final String SUBJECT_UNCOMPLETED_TASKS = "You have %d unfinished tasks remaining";
    private static final String SUBJECT_COMPLETED_TASKS = "Yesterday you completed %d tasks";

    private static final String UNCOMPLETED_TASKS_PARAM = "uncompletedTasks";
    private static final String COMPLETED_TASKS_PARAM = "completedTasks";
    private static final String SUBJECT_PARAM = "subject";

    @Override
    public EmailSendingRequest generateForUserInPeriod(User user, Instant start, Instant end) {
        List<UserTask> uncompletedTasks = new ArrayList<>();
        List<UserTask> completedTasks = new ArrayList<>();
        categorizeTasks(user.getTasks(), uncompletedTasks, completedTasks, start, end);

        return generateRequest(user.getUsername(), uncompletedTasks, completedTasks);
    }

    private void categorizeTasks(List<UserTask> tasks, List<UserTask> uncompletedTasks,
                                 List<UserTask> completedTasks, Instant start, Instant end) {
        for (UserTask task : tasks) {
            if (!task.isCompleted()) {
                uncompletedTasks.add(task);
            } else if (task.getCompletedAt().isAfter(start) && task.getCompletedAt().isBefore(end)) {
                completedTasks.add(task);
            }
        }
    }

    private EmailSendingRequest generateRequest(String email,
                                                List<UserTask> uncompletedTasks,
                                                List<UserTask> completedTasks) {
        Map<String, String> params = new HashMap<>();
        params.put(SUBJECT_PARAM, generateSubject(completedTasks.size(), uncompletedTasks.size()));
        if (!uncompletedTasks.isEmpty()) {
            params.put(UNCOMPLETED_TASKS_PARAM, getFormattedTasksTitles(uncompletedTasks));
        }
        if (!completedTasks.isEmpty()) {
            params.put(COMPLETED_TASKS_PARAM, getFormattedTasksTitles(completedTasks));
        }
        return new EmailSendingRequest(email, TASK_REPORT_TYPE, params);
    }

    private String getFormattedTasksTitles(List<UserTask> tasks) {
        return tasks.stream()
                .limit(TASK_LIMIT)
                .map(UserTask::getTitle)
                .collect(Collectors.joining("\n"));
    }

    private String generateSubject(int completedTasksNumber, int uncompletedTasksNumber) {
        if (uncompletedTasksNumber != 0 && completedTasksNumber != 0) {
            return SUBJECT_ALL_TASKS.formatted(completedTasksNumber, uncompletedTasksNumber);
        }
        if (uncompletedTasksNumber != 0) {
            return SUBJECT_UNCOMPLETED_TASKS.formatted(uncompletedTasksNumber);
        }
        if (completedTasksNumber != 0) {
            return SUBJECT_COMPLETED_TASKS.formatted(completedTasksNumber);
        }
        return "You have no unfinished tasks, and yesterday you didn’t complete any tasks.";
    }
}
