package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.exception.UsernameAlreadyTakenException;
import com.tasktracker.backend.kafka.EmailKafkaProducer;
import com.tasktracker.backend.mapper.UserMapper;
import com.tasktracker.backend.repository.UserRepository;
import com.tasktracker.backend.security.model.CustomUserDetails;
import com.tasktracker.backend.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailKafkaProducer emailKafkaProducer;

    @Override
    public String register(RegisterRequest registerRequest) {
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);

            CustomUserDetails userDetails = new CustomUserDetails(user, new ArrayList<>());
            String token = jwtService.generateToken(userDetails);

            emailKafkaProducer.sendWelcomeEmail(userDetails.getUsername());

            return token;

        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new UsernameAlreadyTakenException("This email is already taken");
            }
            throw e;
        }
    }
}
