package com.example.orderservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {

    @RabbitListener(queues = "order.status.queue")
    public void handleOrderStatusMessage(OrderStatusMessage message) {
        System.out.println("*".repeat(25));
        System.out.println("🎯 MessageQueue: Received order status update:");
        System.out.println("Order ID: " + message.getOrderId());
        System.out.println("New Status: " + message.getStatus());
        System.out.println("*".repeat(25));
    }
}
