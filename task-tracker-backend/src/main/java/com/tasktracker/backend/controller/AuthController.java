package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.auth.LoginRequest;
import com.tasktracker.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwtToken = authService.authenticate(loginRequest);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwtToken)
                .body("OK");
    }
}
