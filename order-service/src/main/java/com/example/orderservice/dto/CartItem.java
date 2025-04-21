package com.example.orderservice.dto;

import lombok.Data;

@Data
public class CartItem {
    private Long itemId;
    private Integer quantity;
}