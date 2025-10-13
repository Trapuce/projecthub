package com.trapuce.projectHub.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filtre de rate limiting basé sur l'IP et l'endpoint
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitingFilter implements Filter {

    // Cache pour stocker les compteurs de requêtes par IP
    private final Cache<String, AtomicInteger> requestCounts = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofMinutes(1))
            .build();

    // Cache pour les IPs bloquées
    private final Cache<String, Boolean> blockedIPs = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    // Limites par endpoint
    private static final int AUTH_LIMIT = 5; // 5 tentatives par minute pour l'auth
    private static final int GENERAL_LIMIT = 100; // 100 requêtes par minute pour les autres endpoints
    private static final int FILE_UPLOAD_LIMIT = 10; // 10 uploads par minute

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIP = getClientIP(httpRequest);
        String endpoint = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Vérifier si l'IP est bloquée
        if (blockedIPs.getIfPresent(clientIP) != null) {
            log.warn("Requête bloquée - IP bloquée: {}, Endpoint: {}", clientIP, endpoint);
            sendRateLimitResponse(httpResponse, "IP temporairement bloquée", 600);
            return;
        }

        // Déterminer la limite selon l'endpoint
        int limit = getRateLimit(endpoint, method);
        String rateLimitKey = clientIP + ":" + endpoint;

        // Vérifier le rate limit
        AtomicInteger count = requestCounts.get(rateLimitKey, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();

        if (currentCount > limit) {
            log.warn("Rate limit dépassé - IP: {}, Endpoint: {}, Count: {}/{}", 
                    clientIP, endpoint, currentCount, limit);
            
            // Bloquer l'IP si trop de violations
            if (currentCount > limit * 2) {
                blockedIPs.put(clientIP, true);
                log.error("IP bloquée pour violations répétées: {}", clientIP);
            }
            
            sendRateLimitResponse(httpResponse, "Trop de requêtes", 60);
            return;
        }

        // Ajouter les en-têtes de rate limiting
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, limit - currentCount)));
        httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 60000));

        chain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }

    private int getRateLimit(String endpoint, String method) {
        // Endpoints d'authentification - plus restrictifs
        if (endpoint.contains("/api/auth/login") || endpoint.contains("/api/auth/register")) {
            return AUTH_LIMIT;
        }
        
        // Upload de fichiers - limite modérée
        if (endpoint.contains("/api/files/upload") && "POST".equals(method)) {
            return FILE_UPLOAD_LIMIT;
        }
        
        // Endpoints sensibles
        if (endpoint.contains("/api/users") && "POST".equals(method)) {
            return 10; // Création d'utilisateurs
        }
        
        if (endpoint.contains("/api/projects") && "POST".equals(method)) {
            return 20; // Création de projets
        }
        
        // Limite générale pour tous les autres endpoints
        return GENERAL_LIMIT;
    }

    private void sendRateLimitResponse(HttpServletResponse response, String message, int retryAfterSeconds) 
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"%s\",\"retryAfter\":%d}",
                message, retryAfterSeconds
        );
        
        response.getWriter().write(jsonResponse);
    }
}
