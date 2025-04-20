package com.example.orderservice.security;

import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
public class OrderSecurity {

    @Autowired
    private OrderRepository orderRepository;

    public boolean isOwner(Long orderId, String username) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUsername().equals(username))
                .orElse(false);
    }
}