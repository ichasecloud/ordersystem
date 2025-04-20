package com.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String itemId;
    private Integer quantity;
}
