package com.tasktracker.scheduler.report;

import com.tasktracker.scheduler.dto.EmailSendingRequest;
import com.tasktracker.scheduler.entity.User;
import com.tasktracker.scheduler.entity.UserTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskReportGenerator {

    private static final int TASK_LIMIT = 5;
    private static final String TASK_REPORT_TYPE = "TASK_REPORT";

    public static EmailSendingRequest generateForUserInPeriod(User user, Instant start, Instant end) {
        List<UserTask> uncompletedTasks = new ArrayList<>();
        List<UserTask> completedTasks = new ArrayList<>();
        categorizeTasks(user.getTasks(), uncompletedTasks, completedTasks, start, end);

        return generateRequest(user.getUsername(), uncompletedTasks, completedTasks);
    }

    private static void categorizeTasks(List<UserTask> tasks, List<UserTask> uncompletedTasks,
                                 List<UserTask> completedTasks, Instant start, Instant end) {
        for (UserTask task : tasks) {
            if (!task.isCompleted()) {
                uncompletedTasks.add(task);
            } else if (task.getCompletedAt().isAfter(start) && task.getCompletedAt().isBefore(end)) {
                completedTasks.add(task);
            }
        }
    }

    private static EmailSendingRequest generateRequest(String email,
                                                       List<UserTask> uncompletedTasks,
                                                       List<UserTask> completedTasks) {
        Map<String, String> params = new HashMap<>();
        params.put("subject", generateSubject(completedTasks.size(), uncompletedTasks.size()));
        if (!uncompletedTasks.isEmpty()) {
            params.put("uncompletedTasks", getFormattedTasksTitles(uncompletedTasks));
        }
        if (!completedTasks.isEmpty()) {
            params.put("completedTasks", getFormattedTasksTitles(completedTasks));
        }
        return new EmailSendingRequest(email, TASK_REPORT_TYPE, params);
    }

    private static String getFormattedTasksTitles(List<UserTask> tasks) {
        return tasks.stream()
                .limit(TASK_LIMIT)
                .map(UserTask::getTitle)
                .collect(Collectors.joining("\n"));
    }

    private static String generateSubject(int completedTasksNumber, int uncompletedTasksNumber) {
        if (uncompletedTasksNumber != 0 && completedTasksNumber != 0) {
            return "Yesterday you completed %d tasks, with %d tasks remaining unfinished"
                    .formatted(completedTasksNumber, uncompletedTasksNumber);
        }
        if (uncompletedTasksNumber != 0) {
            return "You have %d unfinished tasks remaining".formatted(uncompletedTasksNumber);
        }
        if (completedTasksNumber != 0) {
            return "Yesterday you completed %d tasks".formatted(completedTasksNumber);
        }
        return "You have no unfinished tasks, and yesterday you didn’t complete any tasks.";
    }
}
