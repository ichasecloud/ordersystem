package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already in use");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole().toUpperCase() : "USER");

        return userRepository.save(user);
    }

    public String login(UserDTO userDTO) {
        return userRepository.findByUsername(userDTO.getUsername())
                .map(user -> {
                    boolean match = encoder.matches(userDTO.getPassword(), user.getPassword());
                    if (match) {
                        return "Logged in successfully. Role: " + user.getRole();
                    } else {
                        return "Invalid password";
                    }
                })
                .orElse("Username not found.");
    }

    @Autowired
    private JwtUtil jwtUtil;
    public String loginWithJwt(UserDTO userDTO) {
        return userRepository.findByUsername(userDTO.getUsername())
                .map(user -> {
                    if (encoder.matches(userDTO.getPassword(), user.getPassword())) {
                        return jwtUtil.generateToken(user.getUsername(), user.getRole());
                    } else {
                        throw new RuntimeException("Invalid password");
                    }
                })
                .orElseThrow(() -> new RuntimeException("Username not found"));
    }
}
