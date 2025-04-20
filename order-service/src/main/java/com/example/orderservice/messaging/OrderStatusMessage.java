package com.example.orderservice.messaging;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class OrderStatusMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String status;
}
