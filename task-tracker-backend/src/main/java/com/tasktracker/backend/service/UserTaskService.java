package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.UserTaskResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface UserTaskService {

    List<UserTaskResponse> getTasksForCurrentUser(Jwt jwt);

}
