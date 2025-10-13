package com.trapuce.projectHub.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service de monitoring de sécurité pour détecter les activités suspectes
 */
@Slf4j
@Service
public class SecurityMonitoringService {

    // Cache pour les tentatives de connexion échouées
    private final Cache<String, AtomicInteger> failedLoginAttempts = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(15))
            .build();

    // Cache pour les activités suspectes
    private final Cache<String, SecurityEvent> securityEvents = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofHours(24))
            .build();

    // Cache pour les IPs suspectes
    private final Cache<String, AtomicInteger> suspiciousIPs = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    public void recordFailedLogin(String email, String ipAddress) {
        String key = ipAddress + ":" + email;
        AtomicInteger attempts = failedLoginAttempts.get(key, k -> new AtomicInteger(0));
        int currentAttempts = attempts.incrementAndGet();

        log.warn("Tentative de connexion échouée - Email: {}, IP: {}, Tentatives: {}", 
                email, ipAddress, currentAttempts);

        // Enregistrer l'événement de sécurité
        recordSecurityEvent(ipAddress, "FAILED_LOGIN", 
                String.format("Tentative de connexion échouée pour %s (tentative %d)", email, currentAttempts));

        // Bloquer après 5 tentatives
        if (currentAttempts >= 5) {
            log.error("Trop de tentatives de connexion échouées - IP bloquée: {}, Email: {}", ipAddress, email);
            recordSecurityEvent(ipAddress, "IP_BLOCKED", 
                    String.format("IP bloquée après %d tentatives de connexion échouées", currentAttempts));
        }
    }

    public void recordSuccessfulLogin(String email, String ipAddress) {
        // Réinitialiser les tentatives échouées après une connexion réussie
        String key = ipAddress + ":" + email;
        failedLoginAttempts.invalidate(key);
        
        log.info("Connexion réussie - Email: {}, IP: {}", email, ipAddress);
        recordSecurityEvent(ipAddress, "SUCCESSFUL_LOGIN", 
                String.format("Connexion réussie pour %s", email));
    }

    public void recordSuspiciousActivity(String ipAddress, String activity, String details) {
        AtomicInteger count = suspiciousIPs.get(ipAddress, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();

        log.warn("Activité suspecte détectée - IP: {}, Activité: {}, Détails: {}, Count: {}", 
                ipAddress, activity, details, currentCount);

        recordSecurityEvent(ipAddress, "SUSPICIOUS_ACTIVITY", 
                String.format("%s: %s (occurrence %d)", activity, details, currentCount));

        // Alerte si trop d'activités suspectes
        if (currentCount >= 10) {
            log.error("IP très suspecte détectée - IP: {}, Activités suspectes: {}", ipAddress, currentCount);
            recordSecurityEvent(ipAddress, "HIGH_RISK_IP", 
                    String.format("IP à haut risque avec %d activités suspectes", currentCount));
        }
    }

    public void recordSecurityEvent(String ipAddress, String eventType, String description) {
        String eventId = ipAddress + ":" + System.currentTimeMillis();
        SecurityEvent event = new SecurityEvent(
                eventId,
                ipAddress,
                eventType,
                description,
                LocalDateTime.now()
        );
        
        securityEvents.put(eventId, event);
        
        // Log pour monitoring externe
        log.info("SECURITY_EVENT: {} - IP: {} - Type: {} - Description: {}", 
                eventId, ipAddress, eventType, description);
    }

    public boolean isIPBlocked(String ipAddress) {
        String key = ipAddress + ":blocked";
        return securityEvents.getIfPresent(key) != null;
    }

    public int getFailedLoginAttempts(String ipAddress, String email) {
        String key = ipAddress + ":" + email;
        AtomicInteger attempts = failedLoginAttempts.getIfPresent(key);
        return attempts != null ? attempts.get() : 0;
    }

    public int getSuspiciousActivityCount(String ipAddress) {
        AtomicInteger count = suspiciousIPs.getIfPresent(ipAddress);
        return count != null ? count.get() : 0;
    }

    public void clearFailedLoginAttempts(String ipAddress, String email) {
        String key = ipAddress + ":" + email;
        failedLoginAttempts.invalidate(key);
    }

    // Classe interne pour représenter un événement de sécurité
    public static class SecurityEvent {
        private final String eventId;
        private final String ipAddress;
        private final String eventType;
        private final String description;
        private final LocalDateTime timestamp;

        public SecurityEvent(String eventId, String ipAddress, String eventType, String description, LocalDateTime timestamp) {
            this.eventId = eventId;
            this.ipAddress = ipAddress;
            this.eventType = eventType;
            this.description = description;
            this.timestamp = timestamp;
        }

        // Getters
        public String getEventId() { return eventId; }
        public String getIpAddress() { return ipAddress; }
        public String getEventType() { return eventType; }
        public String getDescription() { return description; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
