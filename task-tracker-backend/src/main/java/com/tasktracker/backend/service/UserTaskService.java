package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.UserTaskCreateRequest;
import com.tasktracker.backend.dto.UserTaskResponse;
import com.tasktracker.backend.dto.UserTaskUpdateRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface UserTaskService {

    List<UserTaskResponse> getTasksForCurrentUser(Jwt jwt);

    UserTaskResponse addTask(UserTaskCreateRequest request, Jwt jwt);

    UserTaskResponse updateTask(long taskId, UserTaskUpdateRequest request, Jwt jwt);

    void deleteTask(long taskId, Jwt jwt);

    UserTaskResponse getTask(long taskId, Jwt jwt);
}
