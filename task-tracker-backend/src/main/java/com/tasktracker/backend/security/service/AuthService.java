package com.tasktracker.backend.security.service;

import com.tasktracker.backend.dto.LoginRequest;

public interface AuthService {

    String authenticate(LoginRequest loginRequest);

}
