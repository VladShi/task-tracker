package com.tasktracker.backend.security.service;

import com.tasktracker.backend.dto.request.LoginRequest;

public interface AuthService {

    String authenticate(LoginRequest loginRequest);

}
