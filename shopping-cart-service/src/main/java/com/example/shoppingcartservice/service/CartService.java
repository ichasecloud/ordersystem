package com.example.shoppingcartservice.service;

import com.example.shoppingcartservice.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    @Autowired
    private RedisTemplate<String, Map<Long, Integer>> redisTemplate;

    private String getKey(String username) {
        return "cart:" + username;
    }

    public void addItem(String username, CartItem item) {
        String key = getKey(username);

        Map<Long, Integer> cart = redisTemplate.opsForValue().get(key);
        if (cart == null) cart = new HashMap<>();
        cart.put(item.getItemId(), cart.getOrDefault(item.getItemId(), 0) + item.getQuantity());
        redisTemplate.opsForValue().set(key, cart);
    }

    public void removeItem(String username, Long itemId) {
        String key = getKey(username);
        Map<Long, Integer> cart = redisTemplate.opsForValue().get(key);
        if (cart != null) {
            cart.remove(itemId);
            redisTemplate.opsForValue().set(key, cart);
        }
    }

    public Map<Long, Integer> getCart(String username) {
        String key = getKey(username);
        Map<Long, Integer> cart = redisTemplate.opsForValue().get(key);
        return cart != null ? cart : new HashMap<>();
    }

    public void clearCart(String username) {
        String key = getKey(username);
        redisTemplate.delete(key);
    }
}
