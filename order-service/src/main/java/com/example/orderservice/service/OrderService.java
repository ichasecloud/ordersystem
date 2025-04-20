package com.example.orderservice.service;

import com.example.orderservice.dto.OrderItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.messaging.OrderMessagePublisher;
import com.example.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository OrderRepository;

    @Autowired
    private OrderMessagePublisher messagePublisher;
    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Order createOrder(String username, OrderRequest request) {
        Order order = new Order();
        order.setUsername(username);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> itemList = new ArrayList<>();
        for (OrderItemRequest reqItem : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setItemId(reqItem.getItemId());
            item.setQuantity(reqItem.getQuantity());
            item.setOrder(order);
            itemList.add(item);
        }

        order.setItems(itemList);
        Order saved = OrderRepository.save(order);

        // Publish the message to RabbitMQ
        messagePublisher.sendStatusUpdate(saved.getId(), "CREATED");

        return saved;
    }

    @Transactional
    public Order markAsPaid(Long orderId) {
        Order order = OrderRepository.findById(orderId)
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
