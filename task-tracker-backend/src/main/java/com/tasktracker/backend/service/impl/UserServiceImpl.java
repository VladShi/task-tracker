package com.tasktracker.backend.service.impl;

import com.tasktracker.backend.dto.request.RegisterRequest;
import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.exception.UsernameAlreadyTakenException;
import com.tasktracker.backend.mapper.UserMapper;
import com.tasktracker.backend.repository.UserRepository;
import com.tasktracker.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User register(RegisterRequest registerRequest) {
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            return user;

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new UsernameAlreadyTakenException("This email is already taken");
            }
            throw e;
        }
    }
}
