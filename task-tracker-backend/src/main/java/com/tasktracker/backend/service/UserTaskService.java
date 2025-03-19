package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.request.UserTaskCreateRequest;
import com.tasktracker.backend.dto.response.UserTaskResponse;
import com.tasktracker.backend.dto.request.UserTaskUpdateRequest;

import java.util.List;

public interface UserTaskService {

    List<UserTaskResponse> findAllByUserId(long userId);

    UserTaskResponse add(UserTaskCreateRequest request, long userId);

    UserTaskResponse update(long taskId, UserTaskUpdateRequest request, long userId);

    void delete(long taskId, long userId);

    UserTaskResponse get(long taskId, long userId);
}
