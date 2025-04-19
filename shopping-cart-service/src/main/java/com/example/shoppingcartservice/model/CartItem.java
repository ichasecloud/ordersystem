package com.example.shoppingcartservice.model;

import lombok.Data;

@Data
public class CartItem {
    private Long itemId;
    private Integer quantity;
}
