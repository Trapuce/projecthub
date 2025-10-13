package com.trapuce.projectHub.service;

import com.trapuce.projectHub.dto.response.UserResponse;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.Role;
import com.trapuce.projectHub.enums.UserStatus;
import com.trapuce.projectHub.exception.ProjectAccessDeniedException;
import com.trapuce.projectHub.exception.ResourceNotFoundException;
import com.trapuce.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToUserResponse);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return convertToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        log.info("Searching users with term: {}", search);
        Page<User> users = userRepository.findBySearchTerm(search, pageable);
        return users.map(this::convertToUserResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable) {
        log.info("Fetching users by status: {}", status);
        Page<User> users = userRepository.findByStatus(status, pageable);
        return users.map(this::convertToUserResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        log.info("Fetching users by role: {}", role);
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(this::convertToUserResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByDepartment(String department, Pageable pageable) {
        log.info("Fetching users by department: {}", department);
        Page<User> users = userRepository.findByDepartment(department, pageable);
        return users.map(this::convertToUserResponse);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByProject(Long projectId) {
        log.info("Fetching users by project ID: {}", projectId);
        List<User> users = userRepository.findByProjectId(projectId);
        return users.stream().map(this::convertToUserResponse).toList();
    }
    
    public UserResponse updateUser(Long id, UserResponse userResponse, String currentUserEmail) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Check if user can update this profile
        if (!user.getEmail().equals(currentUserEmail) && !isAdmin(currentUserEmail)) {
            throw new ProjectAccessDeniedException("You can only update your own profile");
        }
        
        user.setFirstName(userResponse.getFirstName());
        user.setLastName(userResponse.getLastName());
        user.setPhone(userResponse.getPhone());
        user.setDepartment(userResponse.getDepartment());
        user.setAvatar(userResponse.getAvatar());
        
        // Only admin can change role and status
        if (isAdmin(currentUserEmail)) {
            user.setRole(userResponse.getRole());
            user.setStatus(userResponse.getStatus());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return convertToUserResponse(updatedUser);
    }
    
    public void updateUserPassword(Long id, String newPassword, String currentUserEmail) {
        log.info("Updating password for user ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Check if user can update this password
        if (!user.getEmail().equals(currentUserEmail) && !isAdmin(currentUserEmail)) {
            throw new ProjectAccessDeniedException("You can only update your own password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user ID: {}", id);
    }
    
    public void deleteUser(Long id, String currentUserEmail) {
        log.info("Deleting user with ID: {}", id);
        
        if (!isAdmin(currentUserEmail)) {
            throw new ProjectAccessDeniedException("Only admin can delete users");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", id);
    }
    
    public void deactivateUser(Long id, String currentUserEmail) {
        log.info("Deactivating user with ID: {}", id);
        
        if (!isAdmin(currentUserEmail)) {
            throw new ProjectAccessDeniedException("Only admin can deactivate users");
        }
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public long getUserCountByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }
    
    private boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getRole() == Role.ADMIN;
    }
    
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.getStatus(),
            user.getAvatar(),
            user.getPhone(),
            user.getDepartment(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
