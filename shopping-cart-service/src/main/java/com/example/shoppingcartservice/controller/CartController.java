package com.example.shoppingcartservice.controller;

import com.example.shoppingcartservice.model.CartItem;
import com.example.shoppingcartservice.service.CartService;
import com.example.shoppingcartservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
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

    @PostMapping("/add")
    public String add(@RequestBody CartItem item, @RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        cartService.addItem(user, item);
        return "Added to cart.";
    }

    @DeleteMapping("/remove/{itemID}")
    public String remove(@PathVariable Long itemID,
                         @RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        cartService.removeItem(user, itemID);
        return "Removed from cart.";
    }

    @GetMapping
    public Map<Long, Integer> get(@RequestHeader("Authorization") String authHeader) {
        String user = extractUser(authHeader);
        return cartService.getCart(user);
    }
}
