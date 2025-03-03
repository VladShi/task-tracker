package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.auth.RegisterRequest;

public interface UserService {

    void register(RegisterRequest registerRequest);

}
