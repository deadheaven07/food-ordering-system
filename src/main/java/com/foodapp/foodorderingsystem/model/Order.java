package com.foodapp.foodorderingsystem.model;

import com.foodapp.foodorderingsystem.model.enums.OrderStatus;
import com.foodapp.foodorderingsystem.model.enums.SelectionStrategyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int id;
    private String user;
    private Map<String, Integer> items;
    private OrderStatus status;
    private String assignedRestaurant;
    private SelectionStrategyType strategy;
}
