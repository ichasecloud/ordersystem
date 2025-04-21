package com.example.menuservice.config;

import com.example.menuservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
// Enable support for @PreAuthorize and other method-level security annotations
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF since we're using token-based (stateless) authentication
                .csrf(csrf -> csrf.disable())

                // Use stateless session management (no server-side session)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // ✅ Allow unauthenticated access to Swagger documentation
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // ✅ Allow unauthenticated access to GET menu items
                        .requestMatchers(HttpMethod.GET, "/menu/items", "/menu/items/*").permitAll()

                        // ✅ Allow unauthenticated access to image resources
                        .requestMatchers("/menu/images/**").permitAll()

                        // ❌ Require authentication for all other requests
                        .anyRequest().authenticated()
                )

                // Register JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}