package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.UserInfoResponse;
import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")  // TODO подумать вынести /api в абстрактный базовый класс ApiController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok("OK");
    }

    @GetMapping()
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new UserInfoResponse(jwt.getClaim("userId"), jwt.getSubject()));
    }
}
