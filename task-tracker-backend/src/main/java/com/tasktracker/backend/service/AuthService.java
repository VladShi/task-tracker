package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.auth.LoginRequest;

public interface AuthService {

    String authenticate(LoginRequest loginRequest);

}
