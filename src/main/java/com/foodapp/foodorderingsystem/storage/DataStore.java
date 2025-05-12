package com.foodapp.foodorderingsystem.storage;

import com.foodapp.foodorderingsystem.model.Restaurant;
import com.foodapp.foodorderingsystem.model.User;
import com.foodapp.foodorderingsystem.model.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class DataStore {
    public Map<String, Restaurant> restaurants = new HashMap<>();
    public Map<String, User> users = new HashMap<>();
    public List<Order> orders = new ArrayList<>();
    public AtomicInteger orderIdCounter = new AtomicInteger(1);
}
