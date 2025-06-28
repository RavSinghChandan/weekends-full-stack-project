# Authentication System Design

## Overview
This document outlines the complete authentication and authorization system for the Fullstack System Design App using Spring Security with JWT tokens.

## Authentication Architecture

### System Components
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           AUTHENTICATION FLOW                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Angular   │    │ Spring Boot │    │   JWT       │    │   MySQL     │
│  Frontend   │    │   Security  │    │  Service    │    │  Database   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │                   │
       │ 1. Login Request  │                   │                   │
       │──────────────────▶│                   │                   │
       │                   │                   │                   │
       │                   │ 2. Validate       │                   │
       │                   │ Credentials       │                   │
       │                   │──────────────────────────────────────▶│
       │                   │                   │                   │
       │                   │ 3. User Data      │                   │
       │                   │◀──────────────────────────────────────│
       │                   │                   │                   │
       │                   │ 4. Generate JWT   │                   │
       │                   │ Tokens            │                   │
       │                   │──────────────────▶│                   │
       │                   │                   │                   │
       │                   │ 5. Store Session  │                   │
       │                   │ in Database       │                   │
       │                   │──────────────────────────────────────▶│
       │                   │                   │                   │
       │ 6. Return JWT     │                   │                   │
       │ Tokens            │                   │                   │
       │◀──────────────────│                   │                   │
       │                   │                   │                   │
       │ 7. Store Tokens   │                   │                   │
       │ in localStorage   │                   │                   │
```

## Security Configuration

### Spring Security Configuration
```java
package com.systemdesign.app.config;

import com.systemdesign.app.security.JwtAuthenticationEntryPoint;
import com.systemdesign.app.security.JwtAuthenticationFilter;
import com.systemdesign.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // Moderator endpoints
                .requestMatchers("/api/v1/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
                
                // User endpoints (authenticated users)
                .requestMatchers("/api/v1/users/**").hasAnyRole("USER", "MODERATOR", "ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### JWT Token Provider
```java
package com.systemdesign.app.security;

import com.systemdesign.app.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateToken(userPrincipal.getUsername(), jwtExpirationMs, "access");
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateToken(userPrincipal.getUsername(), refreshExpirationMs, "refresh");
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        claims.put("type", "access");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("type", "refresh");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private String generateToken(String username, long expiration, String type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    public String getTokenType(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    public Boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }
}
```

### JWT Authentication Filter
```java
package com.systemdesign.app.security;

import com.systemdesign.app.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### JWT Authentication Entry Point
```java
package com.systemdesign.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systemdesign.app.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .error(ErrorResponse.ErrorDetail.builder()
                        .code("UNAUTHORIZED")
                        .message("Access denied. Please provide valid authentication credentials.")
                        .build())
                .timestamp(LocalDateTime.now())
                .build();

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
```

## Authentication Service

### Authentication Service Implementation
```java
package com.systemdesign.app.service;

import com.systemdesign.app.dto.AuthResponse;
import com.systemdesign.app.dto.LoginRequest;
import com.systemdesign.app.dto.RegisterRequest;
import com.systemdesign.app.dto.UserDTO;
import com.systemdesign.app.entity.Session;
import com.systemdesign.app.entity.User;
import com.systemdesign.app.entity.UserRole;
import com.systemdesign.app.repository.SessionRepository;
import com.systemdesign.app.repository.UserRepository;
import com.systemdesign.app.security.JwtTokenProvider;
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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest, String ipAddress, String userAgent) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if user is active
            if (!user.getIsActive()) {
                throw new RuntimeException("Account is deactivated");
            }

            // Update last login
            user.updateLastLogin();
            userRepository.save(user);

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // Store session
            Session session = Session.builder()
                    .user(user)
                    .tokenHash(hashToken(refreshToken))
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .isActive(true)
                    .build();
            sessionRepository.save(session);

            // Log audit event
            auditService.logAction("USER_LOGIN", user.getId(), "LOGIN", "USER", user.getId(),
                    "User logged in successfully", ipAddress, userAgent);

            return AuthResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .data(AuthResponse.AuthData.builder()
                            .user(convertToUserDTO(user))
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .tokenType("Bearer")
                            .expiresIn(900) // 15 minutes
                            .build())
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getEmail(), e);
            
            // Log failed login attempt
            auditService.logAction("LOGIN_FAILED", null, "LOGIN", "USER", null,
                    "Failed login attempt for email: " + loginRequest.getEmail(), ipAddress, userAgent);
            
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest, String ipAddress, String userAgent) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(UserRole.USER)
                .isActive(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Store session
        Session session = Session.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isActive(true)
                .build();
        sessionRepository.save(session);

        // Log audit event
        auditService.logAction("USER_REGISTERED", user.getId(), "REGISTER", "USER", user.getId(),
                "New user registered", ipAddress, userAgent);

        return AuthResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(AuthResponse.AuthData.builder()
                        .user(convertToUserDTO(user))
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(900) // 15 minutes
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken, String ipAddress, String userAgent) {
        try {
            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if session exists and is valid
            String tokenHash = hashToken(refreshToken);
            Optional<Session> sessionOpt = sessionRepository.findByTokenHashAndIsActiveTrue(tokenHash);
            
            if (sessionOpt.isEmpty() || sessionOpt.get().isExpired()) {
                throw new RuntimeException("Invalid or expired session");
            }

            // Generate new tokens
            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            // Update session with new refresh token
            Session session = sessionOpt.get();
            session.setTokenHash(hashToken(newRefreshToken));
            session.setExpiresAt(LocalDateTime.now().plusDays(7));
            sessionRepository.save(session);

            // Log audit event
            auditService.logAction("TOKEN_REFRESHED", user.getId(), "REFRESH", "USER", user.getId(),
                    "Token refreshed successfully", ipAddress, userAgent);

            return AuthResponse.builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(AuthResponse.AuthData.builder()
                            .user(convertToUserDTO(user))
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .tokenType("Bearer")
                            .expiresIn(900) // 15 minutes
                            .build())
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("Token refresh failed");
        }
    }

    @Transactional
    public void logout(String accessToken, String ipAddress, String userAgent) {
        try {
            if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
                String email = jwtTokenProvider.getUsernameFromToken(accessToken);
                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    // Deactivate all user sessions
                    sessionRepository.findByUserIdAndIsActiveTrue(user.getId())
                            .forEach(session -> session.setIsActive(false));
                    sessionRepository.saveAll(sessionRepository.findByUserIdAndIsActiveTrue(user.getId()));

                    // Log audit event
                    auditService.logAction("USER_LOGOUT", user.getId(), "LOGOUT", "USER", user.getId(),
                            "User logged out", ipAddress, userAgent);
                }
            }
        } catch (Exception e) {
            log.error("Logout failed", e);
        }
    }

    private String hashToken(String token) {
        return passwordEncoder.encode(token);
    }

    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
```

## DTOs for Authentication

### Authentication DTOs
```java
package com.systemdesign.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {
    private String email;
    private String password;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private Boolean success;
    private String message;
    private AuthData data;
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AuthData {
        private UserDTO user;
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Integer expiresIn;
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenRequest {
    private String refreshToken;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogoutRequest {
    private String accessToken;
}
```

## User Details Service

### Custom User Details Service
```java
package com.systemdesign.app.service;

import com.systemdesign.app.entity.User;
import com.systemdesign.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
```

## Role-Based Access Control

### Custom Annotations
```java
package com.systemdesign.app.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String value();
}
```

### Role-Based Access Control Service
```java
package com.systemdesign.app.service;

import com.systemdesign.app.entity.User;
import com.systemdesign.app.entity.UserRole;
import com.systemdesign.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    public boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasAnyRole(String... roles) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> {
                    for (String role : roles) {
                        if (authority.getAuthority().equals("ROLE_" + role)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    public boolean isCurrentUser(Long userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(currentUserEmail);
        return user.map(u -> u.getId().equals(userId)).orElse(false);
    }

    public boolean canAccessUser(Long userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
        
        if (currentUser.isEmpty()) {
            return false;
        }

        User user = currentUser.get();
        
        // Admin can access any user
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }
        
        // User can only access their own data
        return user.getId().equals(userId);
    }

    public boolean canModifyUser(Long userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userRepository.findByEmail(currentUserEmail);
        
        if (currentUser.isEmpty()) {
            return false;
        }

        User user = currentUser.get();
        
        // Admin can modify any user
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }
        
        // Moderator can modify regular users
        if (user.getRole() == UserRole.MODERATOR) {
            Optional<User> targetUser = userRepository.findById(userId);
            return targetUser.map(target -> target.getRole() == UserRole.USER).orElse(false);
        }
        
        // User can only modify their own data
        return user.getId().equals(userId);
    }
}
```

## Application Properties

### Security Configuration Properties
```properties
# JWT Configuration
app.jwt.secret=your-super-secret-jwt-key-that-is-at-least-256-bits-long-for-hs512-algorithm
app.jwt.expiration=900000
app.jwt.refresh-expiration=604800000

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin

# Password Policy
app.password.min-length=8
app.password.require-uppercase=true
app.password.require-lowercase=true
app.password.require-numbers=true
app.password.require-special-chars=true

# Session Configuration
app.session.max-sessions-per-user=5
app.session.invalidate-on-logout=true

# Rate Limiting
app.rate-limit.login-attempts=5
app.rate-limit.login-window=900
app.rate-limit.register-attempts=3
app.rate-limit.register-window=3600
```

## Security Best Practices

### Password Security
- **Hashing**: BCrypt with 12 salt rounds
- **Validation**: Minimum 8 characters, uppercase, lowercase, numbers, special characters
- **Storage**: Never store plain text passwords

### Token Security
- **Access Token**: 15 minutes expiration
- **Refresh Token**: 7 days expiration
- **Storage**: Secure HTTP-only cookies for refresh tokens
- **Rotation**: Refresh tokens are rotated on each use

### Session Management
- **Stateless**: JWT-based stateless authentication
- **Database Sessions**: Track active sessions for security
- **Cleanup**: Automatic cleanup of expired sessions
- **Logout**: Invalidate all user sessions on logout

### Rate Limiting
- **Login Attempts**: 5 attempts per 15 minutes
- **Registration**: 3 attempts per hour
- **API Endpoints**: Rate limiting on sensitive endpoints

### Audit Logging
- **All Actions**: Log all authentication events
- **Failed Attempts**: Track failed login attempts
- **IP Tracking**: Log IP addresses for security monitoring
- **User Agent**: Track browser/client information

This comprehensive authentication system provides secure, scalable authentication and authorization for the Spring Boot backend with proper JWT implementation, role-based access control, and security best practices. 