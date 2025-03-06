package com.tasktracker.backend.security.service;

import com.tasktracker.backend.security.model.CustomUserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {

    String generateToken(CustomUserDetails userDetails);

    long extractUserId(Jwt jwt);
}
