package com.trapuce.projectHub.dto.response;

import com.trapuce.projectHub.enums.Role;
import com.trapuce.projectHub.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private UserStatus status;
    private String avatar;
    private String phone;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
