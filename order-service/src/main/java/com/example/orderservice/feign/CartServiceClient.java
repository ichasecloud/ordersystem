package com.example.orderservice.feign;

import com.example.orderservice.dto.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

//@FeignClient(name = "shoppingcartservice", url = "http://localhost:8085")
// url is no more needed since eureka will find the address from server
@FeignClient(name = "shopping-cart-service")
public interface CartServiceClient {

    @GetMapping("/cart")
    List<CartItem> getCart(@RequestHeader("Authorization") String token);

    @DeleteMapping("/cart/clear")
    void clearCart(@RequestHeader("Authorization") String token);
}
