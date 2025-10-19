package com.trapuce.projectHub.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtre de sécurité global pour ajouter des en-têtes de sécurité
 */
@Slf4j
// @Component
// @Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log des requêtes suspectes
        logSuspiciousRequests(httpRequest);

        // Ajouter les en-têtes de sécurité
        addSecurityHeaders(httpResponse);

        chain.doFilter(request, response);
    }

    private void logSuspiciousRequests(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String requestURI = request.getRequestURI();
        
        // Détecter les tentatives d'injection SQL
        if (containsSqlInjection(requestURI) || containsSqlInjection(userAgent)) {
            log.warn("Tentative d'injection SQL détectée - IP: {}, URI: {}, User-Agent: {}", 
                    xForwardedFor, requestURI, userAgent);
        }
        
        // Détecter les tentatives XSS
        if (containsXss(requestURI) || containsXss(userAgent)) {
            log.warn("Tentative XSS détectée - IP: {}, URI: {}, User-Agent: {}", 
                    xForwardedFor, requestURI, userAgent);
        }
        
        // Détecter les scanners de vulnérabilités
        if (isVulnerabilityScanner(userAgent)) {
            log.warn("Scanner de vulnérabilités détecté - IP: {}, User-Agent: {}", 
                    xForwardedFor, userAgent);
        }
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Protection contre le clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        
        // Protection XSS
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Protection du type de contenu
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // Politique de référent
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Politique de sécurité du contenu
        response.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none';");
        
        // Permissions Policy
        response.setHeader("Permissions-Policy", 
                "geolocation=(), " +
                "microphone=(), " +
                "camera=(), " +
                "payment=(), " +
                "usb=(), " +
                "magnetometer=(), " +
                "gyroscope=(), " +
                "speaker=()");
        
        // Cache Control pour les réponses sensibles
        if (response.getHeader("Cache-Control") == null) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
    }

    private boolean containsSqlInjection(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("' or '1'='1") ||
               lowerInput.contains("union select") ||
               lowerInput.contains("drop table") ||
               lowerInput.contains("delete from") ||
               lowerInput.contains("insert into") ||
               lowerInput.contains("update set") ||
               lowerInput.contains("--") ||
               lowerInput.contains("/*") ||
               lowerInput.contains("xp_") ||
               lowerInput.contains("sp_");
    }

    private boolean containsXss(String input) {
        if (input == null) return false;
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("<script") ||
               lowerInput.contains("javascript:") ||
               lowerInput.contains("onload=") ||
               lowerInput.contains("onerror=") ||
               lowerInput.contains("onclick=") ||
               lowerInput.contains("onmouseover=") ||
               lowerInput.contains("onfocus=") ||
               lowerInput.contains("onblur=");
    }

    private boolean isVulnerabilityScanner(String userAgent) {
        if (userAgent == null) return false;
        String lowerUserAgent = userAgent.toLowerCase();
        return lowerUserAgent.contains("sqlmap") ||
               lowerUserAgent.contains("nikto") ||
               lowerUserAgent.contains("nmap") ||
               lowerUserAgent.contains("nessus") ||
               lowerUserAgent.contains("openvas") ||
               lowerUserAgent.contains("burp") ||
               lowerUserAgent.contains("zap") ||
               lowerUserAgent.contains("w3af") ||
               lowerUserAgent.contains("acunetix") ||
               lowerUserAgent.contains("netsparker");
    }
}
