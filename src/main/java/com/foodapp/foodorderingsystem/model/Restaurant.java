package com.foodapp.foodorderingsystem.model;

import com.foodapp.foodorderingsystem.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
    private String name;
    private double rating;
    private int maxOrders;
    private Map<String, Integer> menu = new HashMap<>();
    private List<Order> currentOrders = new ArrayList<>();
}
