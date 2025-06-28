package com.healthcare.app.dto;

import com.healthcare.app.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private String message;
} 