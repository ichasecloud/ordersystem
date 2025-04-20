package com.example.menuservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // dish name
    @Column(nullable = false)
    private String name;

    // description
    private String description;

    // price
    @Column(nullable = false)
    private BigDecimal price;

    // category
    private String category;

    // img url
    @Column(name = "image_url")
    private String imageUrl;

    // create time
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    // update time
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
