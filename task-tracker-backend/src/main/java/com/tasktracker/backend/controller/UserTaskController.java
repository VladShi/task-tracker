package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.UserTaskResponse;
import com.tasktracker.backend.service.UserTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class UserTaskController {

    private final UserTaskService userTaskService;

    @GetMapping
    public ResponseEntity<List<UserTaskResponse>> getTasks(@AuthenticationPrincipal Jwt jwt) {
        List<UserTaskResponse> tasks = userTaskService.getTasksForCurrentUser(jwt);
        return ResponseEntity.ok(tasks);
    }
}
