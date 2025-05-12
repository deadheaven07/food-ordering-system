package com.foodapp.foodorderingsystem.strategy;

import com.foodapp.foodorderingsystem.model.Restaurant;

import java.util.Map;

public interface SelectionStrategy {
    Restaurant select(java.util.List<Restaurant> candidates, Map<String, Integer> items);
}
