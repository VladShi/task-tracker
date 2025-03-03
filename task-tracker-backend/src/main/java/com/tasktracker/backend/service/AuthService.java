package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.auth.LoginRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {

    Authentication authenticate(LoginRequest loginRequest);

}
