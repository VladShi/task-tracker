package com.tasktracker.backend.mapper;

import com.tasktracker.backend.dto.UserTaskCreateRequest;
import com.tasktracker.backend.dto.UserTaskResponse;
import com.tasktracker.backend.dto.UserTaskUpdateRequest;
import com.tasktracker.backend.entity.UserTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserTaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserTask toEntity(UserTaskCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(UserTaskUpdateRequest request, @MappingTarget UserTask userTask);

    UserTaskResponse toDto(UserTask userTask);
}
