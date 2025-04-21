package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Service", description = "API for order operations")
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
    @Operation(summary = "Create an order, request must have Authorization")
    public Order createOrder(@RequestHeader("Authorization") String authHeader) {
        String username = extractUser(authHeader);
        return orderService.createOrder(username, authHeader.substring(7));
    }

    @PutMapping("/pay/{orderId}")
    @PreAuthorize("@orderSecurity.isOwner(#orderId, authentication.name)")
    @Operation(summary = "Pay for an order, the user must be the order owner")
    public Order pay(@PathVariable Long orderId) {
        return orderService.markAsPaid(orderId);
    }

    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("@orderSecurity.isOwner(#orderId, authentication.name)")
    @Operation(summary = "Cancel an order, the user must be the order owner")
    public Order cancel(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
