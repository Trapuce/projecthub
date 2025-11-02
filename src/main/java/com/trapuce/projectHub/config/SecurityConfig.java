package com.trapuce.projectHub.config;

import com.trapuce.projectHub.security.JwtAuthenticationFilter;
import com.trapuce.projectHub.security.UserDetailsServiceImpl;
// import com.trapuce.projectHub.security.SecurityHeadersFilter;
// import com.trapuce.projectHub.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
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
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // private final SecurityHeadersFilter securityHeadersFilter;
    // private final RateLimitingFilter rateLimitingFilter;
    
    @Value("${cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private String allowedOrigins;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                // Autoriser toutes les requêtes OPTIONS (preflight CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/security/**").hasRole("ADMIN")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            // .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class)
            // .addFilterBefore(securityHeadersFilter, RateLimitingFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Nettoyer et split les origines depuis la propriété (séparées par des virgules)
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        
        // Utiliser setAllowedOriginPatterns pour plus de flexibilité (supporte les patterns)
        if (origins.isEmpty() || origins.contains("*")) {
            // Si vide ou *, autoriser toutes les origines (pour développement)
            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        } else {
            configuration.setAllowedOriginPatterns(origins);
        }
        
        // Autoriser toutes les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        
        // Autoriser tous les headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Exposer les headers nécessaires pour le frontend
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers"
        ));
        
        // Autoriser les credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache des requêtes preflight pendant 1 heure
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

