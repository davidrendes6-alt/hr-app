package com.hr_manager.auth_service.service;

import com.hr_manager.auth_service.dto.*;
import com.hr_manager.auth_service.entity.User;
import com.hr_manager.auth_service.repository.UserRepository;
import com.hr_manager.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        UserDto userDto = mapToUserDto(user);

        return LoginResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }

    public ValidateResponse validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto userDto = mapToUserDto(user);

        return ValidateResponse.builder()
                .valid(true)
                .user(userDto)
                .build();
    }

    public LogoutResponse logout() {
        // In a stateless JWT implementation, logout is handled client-side by removing the token
        // For a more robust solution, you could implement token blacklisting
        return LogoutResponse.builder()
                .message("Logged out successfully")
                .build();
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .position(user.getPosition())
                .build();
    }
}

