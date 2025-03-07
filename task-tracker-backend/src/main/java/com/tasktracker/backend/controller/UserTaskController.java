package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.UserTaskCreateRequest;
import com.tasktracker.backend.dto.UserTaskResponse;
import com.tasktracker.backend.dto.UserTaskUpdateRequest;
import com.tasktracker.backend.service.UserTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<UserTaskResponse> createTask(@Valid @RequestBody UserTaskCreateRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        UserTaskResponse response = userTaskService.addTask(request, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<UserTaskResponse> updateTask(@PathVariable("taskId") long taskId,
                                                       @Valid @RequestBody UserTaskUpdateRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        UserTaskResponse response = userTaskService.updateTask(taskId, request, jwt);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable("taskId") long taskId,
                                             @AuthenticationPrincipal Jwt jwt) {
        userTaskService.deleteTask(taskId, jwt);
        return ResponseEntity.noContent().build();  // 204
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<UserTaskResponse> getTask(@PathVariable("taskId") long taskId,
                                                    @AuthenticationPrincipal Jwt jwt) {
        UserTaskResponse response = userTaskService.getTask(taskId, jwt);
        return ResponseEntity.ok(response);
    }
}
