package com.example.menuservice.service;

import com.example.menuservice.entity.MenuItem;

public class TestLombok {
    public static void main(String[] args) {
        MenuItem item = new MenuItem();
        item.setId(1L);
        item.setImageUrl("http://example.com/image.jpg");
        System.out.println(item);
    }
}