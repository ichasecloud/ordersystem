package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import com.example.userservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
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
    public User register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDTO userDTO) {
        String token = userService.loginWithJwt(userDTO);
        return Map.of("token", token);
    }

    @GetMapping("/me")
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
}
