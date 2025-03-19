package com.tasktracker.backend.security.service.impl;

import com.tasktracker.backend.dto.request.LoginRequest;
import com.tasktracker.backend.security.model.CustomUserDetails;
import com.tasktracker.backend.security.service.AuthService;
import com.tasktracker.backend.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public String authenticate(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password())
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return jwtService.generateToken(userDetails);
    }
}
