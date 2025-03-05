package com.tasktracker.backend.security.service;

import com.tasktracker.backend.dto.LoginRequest;
import com.tasktracker.backend.security.model.CustomUserDetails;

public interface AuthService {

    String authenticate(LoginRequest loginRequest);

    String generateToken(CustomUserDetails userDetails);
}
