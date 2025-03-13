package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.UserInfoResponse;
import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.security.service.JwtService;
import com.tasktracker.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping()
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String jwtToken = userService.register(registerRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body("OK");
    }

    @GetMapping()
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                new UserInfoResponse(
                        jwtService.extractUserId(jwt),
                        jwt.getSubject()
                ));
    }
}
