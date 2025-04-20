package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUsername(token);
    }

    @PostMapping("/create")
    public Order createOrder(@RequestBody OrderRequest orderRequest,
                             @RequestHeader("Authorization") String authHeader) {
        String username = extractUser(authHeader);
        return orderService.createOrder(username, orderRequest);
    }

    @PutMapping("/pay/{orderId}")
    @PreAuthorize("@orderSecurity.isOwner(#orderId, authentication.name)")
    public Order pay(@PathVariable Long orderId) {
        return orderService.markAsPaid(orderId);
    }

    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("@orderSecurity.isOwner(#orderId, authentication.name)")
    public Order cancel(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
