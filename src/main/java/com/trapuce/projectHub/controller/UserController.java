package com.trapuce.projectHub.controller;

import com.trapuce.projectHub.dto.response.ApiResponse;
import com.trapuce.projectHub.dto.response.UserResponse;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.Role;
import com.trapuce.projectHub.enums.UserStatus;
import com.trapuce.projectHub.repository.UserRepository;
import com.trapuce.projectHub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve paginated list of all users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all users with pagination");
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("Fetching user by ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(Authentication authentication) {
        log.info("Fetching current user profile");
        String email = authentication.getName();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or email")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @Parameter(description = "Search term") @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching users with term: {}", search);
        Page<UserResponse> users = userService.searchUsers(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieve users filtered by status")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByStatus(
            @Parameter(description = "User status") @PathVariable UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching users by status: {}", status);
        Page<UserResponse> users = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieve users filtered by role")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable Role role,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching users by role: {}", role);
        Page<UserResponse> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/department/{department}")
    @Operation(summary = "Get users by department", description = "Retrieve users filtered by department")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByDepartment(
            @Parameter(description = "Department name") @PathVariable String department,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching users by department: {}", department);
        Page<UserResponse> users = userService.getUsersByDepartment(department, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get users by project", description = "Retrieve users associated with a specific project")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByProject(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {
        log.info("Fetching users by project ID: {}", projectId);
        List<UserResponse> users = userService.getUsersByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestBody UserResponse userResponse,
            Authentication authentication) {
        log.info("Updating user with ID: {}", id);
        String currentUserEmail = authentication.getName();
        UserResponse updatedUser = userService.updateUser(id, userResponse, currentUserEmail);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }
    
    @PutMapping("/{id}/password")
    @Operation(summary = "Update user password", description = "Update user password")
    public ResponseEntity<ApiResponse<String>> updateUserPassword(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestBody Map<String, String> passwordRequest,
            Authentication authentication) {
        log.info("Updating password for user ID: {}", id);
        String currentUserEmail = authentication.getName();
        String newPassword = passwordRequest.get("password");
        userService.updateUserPassword(id, newPassword, currentUserEmail);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", "Password updated"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user (admin only)")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            Authentication authentication) {
        log.info("Deleting user with ID: {}", id);
        String currentUserEmail = authentication.getName();
        userService.deleteUser(id, currentUserEmail);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", "User deleted"));
    }
    
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account (admin only)")
    public ResponseEntity<ApiResponse<String>> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            Authentication authentication) {
        log.info("Deactivating user with ID: {}", id);
        String currentUserEmail = authentication.getName();
        userService.deactivateUser(id, currentUserEmail);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", "User deactivated"));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get user count by status")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUserStats() {
        log.info("Fetching user statistics");
        Map<String, Long> stats = Map.of(
            "active", userService.getUserCountByStatus(UserStatus.ACTIVE),
            "inactive", userService.getUserCountByStatus(UserStatus.INACTIVE),
            "pending", userService.getUserCountByStatus(UserStatus.PENDING)
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
