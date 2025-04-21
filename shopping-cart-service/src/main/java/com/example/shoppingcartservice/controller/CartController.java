package com.example.shoppingcartservice.controller;

import com.example.shoppingcartservice.model.CartItem;
import com.example.shoppingcartservice.service.CartService;
import com.example.shoppingcartservice.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@Tag(name = "Shopping Cart", description = "API for cart operations")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing token");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUsername(token);
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/add")
    public String add(@RequestBody CartItem item, @RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        cartService.addItem(user, item);
        return "Added to cart.";
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/remove/{itemID}")
    public String remove(@PathVariable Long itemID,
                         @RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        cartService.removeItem(user, itemID);
        return "Removed from cart.";
    }

    @Operation(summary = "Get user's cart")
    @GetMapping
    public List<CartItem> get(@RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        Map<Long, Integer> cartMap = cartService.getCart(user);

        // transform Map<Long, Integer> to List<CartItem>
        List<CartItem> list = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            CartItem item = new CartItem();
            item.setItemId(entry.getKey());
            item.setQuantity(entry.getValue());
            list.add(item);
        }

        return list;
    }

    @Operation(summary = "Clear user's cart")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared.");
    }
}
