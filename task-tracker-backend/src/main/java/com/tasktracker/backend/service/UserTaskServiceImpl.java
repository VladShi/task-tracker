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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTaskServiceImpl implements UserTaskService {

    private final UserTaskRepository userTaskRepository;
    private final UserTaskMapper mapper;

    @Override
    public List<UserTaskResponse> getTasksForCurrentUser(long userId) {
        List<UserTask> userTasks = userTaskRepository.findTasksByUserId(userId);
        return userTasks.stream().map(mapper::toDto).toList();
    }

    @Override
    public UserTaskResponse addTask(UserTaskCreateRequest request, long userId) {
        UserTask userTask = createNewUserTask(request, userId);
        userTaskRepository.save(userTask);
        return mapper.toDto(userTask);
    }

    private UserTask createNewUserTask(UserTaskCreateRequest request, long userId) {
        UserTask userTask = mapper.toEntity(request);
        userTask.setUser(new User(userId));
        userTask.setCreatedAt(Instant.now());
        userTask.setCompleted(false);
        return userTask;
    }

    @Override
    public UserTaskResponse updateTask(long taskId, UserTaskUpdateRequest request, long userId) {
        UserTask userTask = findTaskById(taskId);
        verifyTaskOwnership(userTask, userId);
        updateTaskFromRequest(userTask, request);
        userTask = userTaskRepository.save(userTask);
        return mapper.toDto(userTask);
    }

    private UserTask findTaskById(long id) {
        return userTaskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);
    }

    private void verifyTaskOwnership(UserTask userTask, long userId) {
        long taskOwnerId = userTask.getUser().getId();
        if (taskOwnerId != userId) {
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

    @Override
    public void deleteTask(long taskId, long userId) {
        UserTask userTask = findTaskById(taskId);
        verifyTaskOwnership(userTask, userId);
        userTaskRepository.delete(userTask);
    }

    @Override
    public UserTaskResponse getTask(long taskId, long userId) {
        UserTask userTask = findTaskById(taskId);
        verifyTaskOwnership(userTask, userId);
        return mapper.toDto(userTask);
    }
}
