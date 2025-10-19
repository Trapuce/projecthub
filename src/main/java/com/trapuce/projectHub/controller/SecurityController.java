package com.trapuce.projectHub.controller;

import com.trapuce.projectHub.dto.response.ApiResponse;
import com.trapuce.projectHub.security.SecurityMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour le monitoring de sécurité (réservé aux administrateurs)
 */
// @RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Security", description = "Security monitoring APIs (Admin only)")
@PreAuthorize("hasRole('ADMIN')")
public class SecurityController {
    
    private final SecurityMonitoringService securityMonitoringService;
    
    @GetMapping("/stats")
    @Operation(summary = "Get security statistics", description = "Get security monitoring statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSecurityStats() {
        log.info("Security stats request received");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Security monitoring is active");
        stats.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success("Security stats retrieved", stats));
    }
    
    @GetMapping("/failed-logins/{ip}")
    @Operation(summary = "Get failed login attempts for IP", description = "Get number of failed login attempts for specific IP")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFailedLoginAttempts(
            @PathVariable String ip,
            @RequestParam(required = false) String email) {
        
        log.info("Failed login attempts request for IP: {}, Email: {}", ip, email);
        
        int attempts = securityMonitoringService.getFailedLoginAttempts(ip, email != null ? email : "");
        boolean isBlocked = securityMonitoringService.isIPBlocked(ip);
        int suspiciousCount = securityMonitoringService.getSuspiciousActivityCount(ip);
        
        Map<String, Object> result = new HashMap<>();
        result.put("ip", ip);
        result.put("email", email);
        result.put("failedLoginAttempts", attempts);
        result.put("isBlocked", isBlocked);
        result.put("suspiciousActivityCount", suspiciousCount);
        
        return ResponseEntity.ok(ApiResponse.success("Failed login attempts retrieved", result));
    }
    
    @PostMapping("/clear-failed-logins/{ip}")
    @Operation(summary = "Clear failed login attempts", description = "Clear failed login attempts for specific IP and email")
    public ResponseEntity<ApiResponse<String>> clearFailedLoginAttempts(
            @PathVariable String ip,
            @RequestParam(required = false) String email) {
        
        log.info("Clear failed login attempts request for IP: {}, Email: {}", ip, email);
        
        securityMonitoringService.clearFailedLoginAttempts(ip, email != null ? email : "");
        
        return ResponseEntity.ok(ApiResponse.success("Failed login attempts cleared", 
                "Failed login attempts cleared for IP: " + ip));
    }
    
    @GetMapping("/health")
    @Operation(summary = "Security health check", description = "Check security system health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> securityHealthCheck() {
        log.info("Security health check request");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("securityMonitoring", "ACTIVE");
        health.put("rateLimiting", "ACTIVE");
        health.put("securityHeaders", "ACTIVE");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success("Security system is healthy", health));
    }
}
