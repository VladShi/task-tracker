package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.UserTaskCreateRequest;
import com.tasktracker.backend.dto.UserTaskResponse;
import com.tasktracker.backend.dto.UserTaskUpdateRequest;
import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.entity.UserTask;
import com.tasktracker.backend.exception.TaskNotFoundException;
import com.tasktracker.backend.exception.TaskOwnershipException;
import com.tasktracker.backend.mapper.UserTaskMapper;
import com.tasktracker.backend.repository.UserTaskRepository;
import com.tasktracker.backend.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTaskServiceImpl implements UserTaskService {

    private final UserTaskRepository userTaskRepository;
    private final JwtService jwtService;
    private final UserTaskMapper mapper;

    @Override
    public List<UserTaskResponse> getTasksForCurrentUser(Jwt jwt) {
        List<UserTask> userTasks = userTaskRepository.findTasksByUserId(jwtService.extractUserId(jwt));
        return userTasks.stream().map(mapper::toDto).toList();
    }

    @Override
    public UserTaskResponse addTask(UserTaskCreateRequest request, Jwt jwt) {
        UserTask userTask = createNewUserTask(request, jwt);
        userTask = userTaskRepository.save(userTask);
        return mapper.toDto(userTask);
    }

    private UserTask createNewUserTask(UserTaskCreateRequest request, Jwt jwt) {
        UserTask userTask = mapper.toEntity(request);
        long currentUserId = jwtService.extractUserId(jwt);
        userTask.setUser(new User(currentUserId));
        userTask.setCreatedAt(Instant.now());
        userTask.setCompleted(false);
        return userTask;
    }

    @Override
    public UserTaskResponse updateTask(long taskId, UserTaskUpdateRequest request, Jwt jwt) {
        UserTask userTask = findTaskById(taskId);
        verifyTaskOwnership(userTask, jwt);
        updateTaskFromRequest(userTask, request);
        userTask = userTaskRepository.save(userTask);
        return mapper.toDto(userTask);
    }

    private UserTask findTaskById(long id) {
        return userTaskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);
    }

    private void verifyTaskOwnership(UserTask userTask, Jwt jwt) {
        long currentUserId = jwtService.extractUserId(jwt);
        long taskOwnerId = userTask.getUser().getId();
        if (taskOwnerId != currentUserId) {
            throw new TaskOwnershipException();
        }
    }

    private void updateTaskFromRequest(UserTask userTask, UserTaskUpdateRequest request) {
        boolean isStatusChanged = userTask.isCompleted() != request.completed();
        mapper.updateEntity(request, userTask);
        if (isStatusChanged) {
            userTask.setCompletedAt(userTask.isCompleted() ? Instant.now() : null);
        }
    }
}
