package com.example.orderservice.service;

import com.example.orderservice.dto.CartItem;
import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.feign.CartServiceClient;
import com.example.orderservice.messaging.OrderMessagePublisher;
import com.example.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMessagePublisher messagePublisher;

    @Autowired
    private CartServiceClient cartServiceClient;

    @Transactional
    public Order createOrder(String username, String token) {

        // 1. get all items in shopping cart
        List<CartItem> cartItems = cartServiceClient.getCart("Bearer " + token);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create order: shopping cart is empty.");
        }

        // 2. create order object
        Order order = new Order();
        order.setUsername(username);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        // 3. set itemList
        List<OrderItem> itemList = cartItems.stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setItemId(cartItem.getItemId().toString());
            item.setQuantity(cartItem.getQuantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(itemList);

        // 4. save order and publish message to RabbitMQ
        Order saved = orderRepository.save(order);
        messagePublisher.sendStatusUpdate(saved.getId(), "CREATED");

        // 5. clear the shopping cart
        cartServiceClient.clearCart("Bearer " + token);

        return saved;
    }

    @Transactional
    public Order markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("PAID");
        orderRepository.save(order);
        messagePublisher.sendStatusUpdate(order.getId(), "PAID");

        return order;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        messagePublisher.sendStatusUpdate(order.getId(), "CANCELLED");

        return order;
    }
}
