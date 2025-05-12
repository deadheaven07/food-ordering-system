package com.foodapp.foodorderingsystem.strategy;

import com.foodapp.foodorderingsystem.model.Restaurant;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class LowestCostStrategy implements SelectionStrategy {

    @Override
    public Restaurant select(List<Restaurant> candidates, Map<String, Integer> items) {
        return candidates.stream()
                .min(Comparator.comparingInt(r -> calculateTotal(r, items)))
                .orElse(null);
    }

    private int calculateTotal(Restaurant r, Map<String, Integer> items) {
        return items.entrySet().stream()
                .mapToInt(e -> r.getMenu().get(e.getKey()) * e.getValue())
                .sum();
    }
}
