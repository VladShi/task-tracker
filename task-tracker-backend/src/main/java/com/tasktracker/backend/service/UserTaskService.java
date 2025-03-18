package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.UserTaskCreateRequest;
import com.tasktracker.backend.dto.UserTaskResponse;
import com.tasktracker.backend.dto.UserTaskUpdateRequest;

import java.util.List;

public interface UserTaskService {

    List<UserTaskResponse> getTasksForCurrentUser(long userId);

    UserTaskResponse addTask(UserTaskCreateRequest request, long userId);

    UserTaskResponse updateTask(long taskId, UserTaskUpdateRequest request, long userId);

    void deleteTask(long taskId, long userId);

    UserTaskResponse getTask(long taskId, long userId);
}
