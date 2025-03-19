package com.tasktracker.backend.service.impl;

import com.tasktracker.backend.dto.request.UserTaskCreateRequest;
import com.tasktracker.backend.dto.response.UserTaskResponse;
import com.tasktracker.backend.dto.request.UserTaskUpdateRequest;
import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.entity.UserTask;
import com.tasktracker.backend.exception.TaskDeletingException;
import com.tasktracker.backend.exception.TaskNotFoundException;
import com.tasktracker.backend.exception.TaskOwnershipException;
import com.tasktracker.backend.mapper.UserTaskMapper;
import com.tasktracker.backend.repository.UserTaskRepository;
import com.tasktracker.backend.service.UserTaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTaskServiceImpl implements UserTaskService {

    private final UserTaskRepository userTaskRepository;
    private final UserTaskMapper taskMapper;

    @Override
    public List<UserTaskResponse> findAllByUserId(long userId) {
        List<UserTask> userTasks = userTaskRepository.findTasksByUserId(userId);
        return taskMapper.toDto(userTasks);
    }

    @Override
    public UserTaskResponse add(UserTaskCreateRequest request, long userId) {
        UserTask userTask = taskMapper.toEntity(request);

        userTask.setUser(new User(userId));
        userTask.setCreatedAt(Instant.now());
        userTask.setCompleted(false);

        userTaskRepository.save(userTask);

        return taskMapper.toDto(userTask);
    }

    @Override
    public UserTaskResponse update(long taskId, UserTaskUpdateRequest request, long userId) {
        UserTask userTask = userTaskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        verifyTaskOwnership(userTask, userId);
        updateTaskFromRequest(userTask, request);
        userTaskRepository.save(userTask);
        return taskMapper.toDto(userTask);
    }
    // TODO избавиться от двойного запроса в бд при обновлении задачи можно только если использовать особенность
    //  postgresql с RETURNING в запросе UPDATE или используя процедуры/функции в бд. Так как UPDATE запрос в
    //  стандартом SQL или JPQL не подразумевает возвращение измененной строки, а возвращает только количество
    //  затронутых строк. Так что, что бы вернуть актуальные данные все равно после UPDATE пришлось бы делать SELECT,
    //  в случае если возложить на базу логику с заполнением completed_at, вместо кода в updateTaskFromRequest.

    private void verifyTaskOwnership(UserTask userTask, long userId) {
        long taskOwnerId = userTask.getUser().getId();
        if (taskOwnerId != userId) {
            throw new TaskOwnershipException();
        }
    }

    private void updateTaskFromRequest(UserTask userTask, UserTaskUpdateRequest request) {
        boolean isStatusChanged = userTask.isCompleted() != request.completed();
        taskMapper.updateEntity(request, userTask);
        if (isStatusChanged) {
            userTask.setCompletedAt(userTask.isCompleted() ? Instant.now() : null);
        }
    }

    @Override
    @Transactional
    public void delete(long taskId, long userId) {
        int deletedRows = userTaskRepository.deleteByIdAndUserId(taskId, userId);
        if (deletedRows == 0) {
            throw new TaskDeletingException();
        }
    }

    @Override
    public UserTaskResponse get(long taskId, long userId) {
        UserTask userTask = userTaskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        verifyTaskOwnership(userTask, userId);
        return taskMapper.toDto(userTask);
    }
}
