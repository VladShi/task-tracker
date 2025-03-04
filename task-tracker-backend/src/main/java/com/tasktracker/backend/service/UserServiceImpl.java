package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.auth.RegisterRequest;
import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.exception.UsernameAlreadyTakenException;
import com.tasktracker.backend.mapper.UserMapper;
import com.tasktracker.backend.repository.UserRepository;
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
    public void register(RegisterRequest registerRequest) {
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new UsernameAlreadyTakenException("This email is already taken");
            }
            throw e;
        }
    }
}
