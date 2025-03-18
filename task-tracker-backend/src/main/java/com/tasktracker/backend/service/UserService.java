package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.entity.User;

public interface UserService {

    User register(RegisterRequest registerRequest);

}
