package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.RegisterRequest;

public interface UserService {

    void register(RegisterRequest registerRequest);

}
