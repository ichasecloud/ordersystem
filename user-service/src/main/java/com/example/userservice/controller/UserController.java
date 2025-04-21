package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import com.example.userservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "User Service", description = "API for user operations")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    // 1. Anyone can register a normal USER
    // 2. If current user want to register a ADMIN role, the request header should have valid JWT
    // and the role in JWT is ADMIN.
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') or #userDTO.role == null or #userDTO.role.toUpperCase() != "
            + "'ADMIN'")
    @Operation(summary = "Register a user, anyone can register a normal user, but only admin can "
            + "register admin.")
    public User register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "login function")
    public Map<String, String> login(@RequestBody UserDTO userDTO) {
        String token = userService.loginWithJwt(userDTO);
        return Map.of("token", token);
    }

    /*
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @Operation(summary = "test function: after getting JWT, return the username and role.")
    public Map<String, String> currentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        if (!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getUserRole(token);

        return Map.of(
                "username", username,
                "role", role
        );
    }
    */

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @Operation(summary = "test function: after getting JWT, return the username and role.")
    public Map<String, String> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the username
        String username = authentication.getName();

        // Get the role
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN");

        return Map.of(
                "username", username,
                "role", role
        );
    }
}
