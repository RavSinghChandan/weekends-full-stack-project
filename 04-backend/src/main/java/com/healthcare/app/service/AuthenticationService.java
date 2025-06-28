package com.healthcare.app.service;

import com.healthcare.app.dto.LoginRequest;
import com.healthcare.app.dto.LoginResponse;
import com.healthcare.app.dto.RegisterRequest;
import com.healthcare.app.dto.RegisterResponse;
import com.healthcare.app.entity.User;
import com.healthcare.app.enums.UserRole;
import com.healthcare.app.exception.BusinessException;
import com.healthcare.app.exception.ResourceNotFoundException;
import com.healthcare.app.repository.UserRepository;
import com.healthcare.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Processing registration request for email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new BusinessException("Email already registered");
        }

        // Validate password strength
        validatePassword(request.getPassword());

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .role(request.getRole() != null ? request.getRole() : UserRole.PATIENT)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .message("User registered successfully")
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (!user.isActive()) {
                log.warn("Login failed: Inactive user - {}", request.getEmail());
                throw new BusinessException("Account is deactivated");
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            log.info("User logged in successfully: {}", user.getEmail());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs())
                    .build();

        } catch (Exception e) {
            log.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw new BusinessException("Invalid email or password");
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        log.info("Processing token refresh request");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Token refresh failed: Invalid refresh token");
            throw new BusinessException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            log.warn("Token refresh failed: Inactive user - {}", email);
            throw new BusinessException("Account is deactivated");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("Token refreshed successfully for user: {}", email);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs())
                .build();
    }

    public void logout(String token) {
        log.info("Processing logout request");
        
        // In a real application, you might want to blacklist the token
        // For now, we'll just log the logout
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtTokenProvider.getEmailFromToken(token);
            log.info("User logged out: {}", email);
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("No authenticated user found");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isCurrentUser(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(userId);
    }

    public boolean hasRole(UserRole role) {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == role;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("Password must be at least 8 characters long");
        }

        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasUpperCase || !hasLowerCase || !hasDigit) {
            throw new BusinessException("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Processing password change request for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Password change failed: Incorrect current password for user ID: {}", userId);
            throw new BusinessException("Current password is incorrect");
        }

        // Validate new password
        validatePassword(newPassword);

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
    }

    public void resetPassword(String email) {
        log.info("Processing password reset request for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // In a real application, you would:
        // 1. Generate a reset token
        // 2. Send an email with the reset link
        // 3. Store the reset token with expiration

        log.info("Password reset email sent to: {}", email);
        // For now, we'll just log the request
    }
} 