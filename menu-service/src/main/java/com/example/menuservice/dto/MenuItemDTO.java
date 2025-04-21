package com.example.menuservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
}