package com.foodapp.foodorderingsystem.service;

import com.foodapp.foodorderingsystem.model.Restaurant;
import com.foodapp.foodorderingsystem.storage.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {

    @Autowired
    private DataStore dataStore;

    // Onboard new restaurant
    public void onboardRestaurant(String name, double rating, int maxOrders, Map<String, Integer> menu) {
        if (dataStore.restaurants.containsKey(name)) {
            System.out.println("Restaurant already exists with name: " + name);
            return;
        }
        Restaurant restaurant = new Restaurant(name, rating, maxOrders, menu, new ArrayList<>());
        dataStore.restaurants.put(name, restaurant);
        System.out.println("Restaurant onboarded: " + name);
    }

    // Add new items (not update existing ones)
    public void addMenuItems(String restaurantName, Map<String, Integer> newItems) {
        Restaurant restaurant = dataStore.restaurants.get(restaurantName);
        if (restaurant == null) {
            System.out.println("Restaurant not found.");
            return;
        }

        newItems.forEach((item, price) -> {
            if (!restaurant.getMenu().containsKey(item)) {
                restaurant.getMenu().put(item, price);
            } else {
                System.out.println("Item already exists: " + item);
            }
        });
    }

    // Update prices of existing items
    public void updateMenuPrices(String restaurantName, Map<String, Integer> updates) {
        Restaurant restaurant = dataStore.restaurants.get(restaurantName);
        if (restaurant == null) {
            System.out.println("Restaurant not found.");
            return;
        }

        updates.forEach((item, price) -> {
            if (restaurant.getMenu().containsKey(item)) {
                restaurant.getMenu().put(item, price);
            } else {
                System.out.println("Item not found in menu for update: " + item);
            }
        });
    }
    // Setter to inject DataStore manually for unit testing
    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

}
