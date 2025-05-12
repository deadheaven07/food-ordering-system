package com.foodapp.foodorderingsystem.service;

import com.foodapp.foodorderingsystem.model.Order;
import com.foodapp.foodorderingsystem.model.Restaurant;
import com.foodapp.foodorderingsystem.model.User;
import com.foodapp.foodorderingsystem.model.enums.OrderStatus;
import com.foodapp.foodorderingsystem.model.enums.SelectionStrategyType;
import com.foodapp.foodorderingsystem.storage.DataStore;
import com.foodapp.foodorderingsystem.strategy.HighestRatingStrategy;
import com.foodapp.foodorderingsystem.strategy.LowestCostStrategy;
import com.foodapp.foodorderingsystem.strategy.SelectionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private DataStore dataStore;
    @Autowired private LowestCostStrategy lowestCostStrategy;
    @Autowired private HighestRatingStrategy highestRatingStrategy;

    public String placeOrder(String user, Map<String, Integer> items, SelectionStrategyType strategyType) {
        // Step 1: Find all available restaurants that can fulfill the order
        List<Restaurant> candidates = dataStore.restaurants.values().stream()
                .filter(r -> r.getCurrentOrders().size() < r.getMaxOrders())
                .filter(r -> r.getMenu().keySet().containsAll(items.keySet()))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return "Cannot assign the order (no restaurant can fulfill)";
        }

        // Step 2: Pick the strategy
        SelectionStrategy strategy = switch (strategyType) {
            case LOWEST_COST -> lowestCostStrategy;
            case HIGHEST_RATING -> highestRatingStrategy;
        };

        // Step 3: Choose restaurant using strategy
        Restaurant selected = strategy.select(candidates, items);
        if (selected == null) return "No restaurant could be selected";

        // 💰 WALLET CHECK AND DEDUCTION
        User u = dataStore.users.get(user);
        if (u == null) return "User not found";

        int totalBill = items.entrySet().stream()
                .mapToInt(e -> selected.getMenu().get(e.getKey()) * e.getValue())
                .sum();

        if (u.getWalletBalance() < totalBill) {
            return "Insufficient wallet balance";
        }

        u.setWalletBalance(u.getWalletBalance() - totalBill);


        // Step 4: Create and assign the order
        int orderId = dataStore.orderIdCounter.getAndIncrement();
        Order order = new Order(orderId, user, items, OrderStatus.ACCEPTED, selected.getName(), strategyType);
        selected.getCurrentOrders().add(order);
        dataStore.orders.add(order);

        return "Order " + orderId + " assigned to " + selected.getName();
    }

    public String completeOrder(int orderId) {
        Optional<Order> orderOpt = dataStore.orders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst();

        if (orderOpt.isEmpty()) return "Order not found";

        Order order = orderOpt.get();
        order.setStatus(OrderStatus.COMPLETED);

        // Remove from restaurant queue
        Restaurant restaurant = dataStore.restaurants.get(order.getAssignedRestaurant());
        if (restaurant != null) {
            restaurant.getCurrentOrders().removeIf(o -> o.getId() == orderId);
        }

        return "Order " + orderId + " marked as COMPLETED";
    }
    // Setter for injecting DataStore in unit tests
    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // Setter for injecting LowestCostStrategy in unit tests
    public void setLowestCostStrategy(LowestCostStrategy lowestCostStrategy) {
        this.lowestCostStrategy = lowestCostStrategy;
    }

    // Setter for injecting HighestRatingStrategy in unit tests
    public void setHighestRatingStrategy(HighestRatingStrategy highestRatingStrategy) {
        this.highestRatingStrategy = highestRatingStrategy;
    }

    // Handles splitting order across multiple restaurants
    public List<String> placeOrderAcrossRestaurants(String user, Map<String, Integer> items) {
        List<String> messages = new ArrayList<>();
        Map<String, Integer> remainingItems = new HashMap<>(items);

        for (Restaurant restaurant : dataStore.restaurants.values()) {
            if (restaurant.getCurrentOrders().size() >= restaurant.getMaxOrders()) continue;

            Map<String, Integer> fulfillableItems = new HashMap<>();
            for (String item : remainingItems.keySet()) {
                if (restaurant.getMenu().containsKey(item)) {
                    fulfillableItems.put(item, remainingItems.get(item));
                }
            }

            if (!fulfillableItems.isEmpty()) {
                int orderId = dataStore.orderIdCounter.getAndIncrement();
                Order order = new Order(orderId, user, fulfillableItems, OrderStatus.ACCEPTED, restaurant.getName(), SelectionStrategyType.LOWEST_COST);
                dataStore.orders.add(order);
                restaurant.getCurrentOrders().add(order);
                messages.add("Order " + orderId + " assigned to " + restaurant.getName());
                fulfillableItems.keySet().forEach(remainingItems::remove);
            }

            if (remainingItems.isEmpty()) break;
        }

        if (!remainingItems.isEmpty()) {
            messages.add("Could not fulfill items: " + remainingItems.keySet());
        }

        return messages;
    }

    public DataStore getDataStore() {
        return dataStore;
    }
}
