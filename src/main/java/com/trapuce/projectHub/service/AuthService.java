package com.trapuce.projectHub.service;

import com.trapuce.projectHub.dto.request.LoginRequest;
import com.trapuce.projectHub.dto.request.RefreshTokenRequest;
import com.trapuce.projectHub.dto.request.RegisterRequest;
import com.trapuce.projectHub.dto.response.AuthResponse;
import com.trapuce.projectHub.dto.response.UserResponse;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.UserStatus;
import com.trapuce.projectHub.exception.BadCredentialsException;
import com.trapuce.projectHub.exception.ResourceAlreadyExistsException;
import com.trapuce.projectHub.exception.ResourceNotFoundException;
import com.trapuce.projectHub.repository.UserRepository;
import com.trapuce.projectHub.security.JwtUtil;
import com.trapuce.projectHub.security.SecurityMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final SecurityMonitoringService securityMonitoringService;
    
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setDepartment(request.getDepartment());
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return generateAuthResponse(savedUser);
    }
    
    public AuthResponse login(LoginRequest request, String clientIP) {
        log.info("Login attempt for email: {} from IP: {}", request.getEmail(), clientIP);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            if (user.getStatus() != UserStatus.ACTIVE) {
                securityMonitoringService.recordFailedLogin(request.getEmail(), clientIP);
                throw new BadCredentialsException("Account is not active");
            }
            
            // Enregistrer la connexion réussie
            securityMonitoringService.recordSuccessfulLogin(request.getEmail(), clientIP);
            log.info("User logged in successfully: {}", user.getEmail());
            return generateAuthResponse(user);
            
        } catch (Exception e) {
            // Enregistrer la tentative de connexion échouée
            securityMonitoringService.recordFailedLogin(request.getEmail(), clientIP);
            log.error("Login failed for email: {} from IP: {}", request.getEmail(), clientIP);
            throw new BadCredentialsException("Invalid email or password");
        }
    }
    
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");
        
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        
        if (!jwtUtil.isRefreshToken(request.getRefreshToken())) {
            throw new BadCredentialsException("Token is not a refresh token");
        }
        
        String email = jwtUtil.extractUsername(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }
        
        log.info("Token refreshed successfully for user: {}", email);
        return generateAuthResponse(user);
    }
    
    private AuthResponse generateAuthResponse(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(new ArrayList<>())
            .build();
        
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        UserResponse userResponse = convertToUserResponse(user);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            "Bearer",
            86400000L, // 24 hours
            userResponse
        );
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
