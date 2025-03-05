package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.RegisterRequest;

public interface UserService {

    String register(RegisterRequest registerRequest);

}
