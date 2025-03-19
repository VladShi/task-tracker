package com.tasktracker.backend.mapper;

import com.tasktracker.backend.dto.request.RegisterRequest;
import com.tasktracker.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "login", target = "username")
    User toEntity(RegisterRequest request);
}
