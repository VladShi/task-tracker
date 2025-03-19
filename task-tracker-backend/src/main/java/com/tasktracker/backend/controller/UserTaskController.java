package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.request.UserTaskCreateRequest;
import com.tasktracker.backend.dto.response.UserTaskResponse;
import com.tasktracker.backend.dto.request.UserTaskUpdateRequest;
import com.tasktracker.backend.security.annotation.PrincipalId;
import com.tasktracker.backend.service.UserTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class UserTaskController {

    private final UserTaskService userTaskService;

    @GetMapping
    public ResponseEntity<List<UserTaskResponse>> getTasks(@PrincipalId long userId) {
        List<UserTaskResponse> tasks = userTaskService.findAllByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<UserTaskResponse> createTask(@Valid @RequestBody UserTaskCreateRequest request,
                                                       @PrincipalId long userId) {
        UserTaskResponse response = userTaskService.add(request, userId);
        URI uri = URI.create("/tasks/%d".formatted(response.id()));
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<UserTaskResponse> updateTask(@PathVariable("taskId") long taskId,
                                                       @Valid @RequestBody UserTaskUpdateRequest request,
                                                       @PrincipalId long userId) {
        UserTaskResponse response = userTaskService.update(taskId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable("taskId") long taskId,
                                             @PrincipalId long userId) {
        userTaskService.delete(taskId, userId);
        return ResponseEntity.noContent().build();  // 204
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<UserTaskResponse> getTask(@PathVariable("taskId") long taskId,
                                                    @PrincipalId long userId) {
        UserTaskResponse response = userTaskService.get(taskId, userId);
        return ResponseEntity.ok(response);
    }
}
