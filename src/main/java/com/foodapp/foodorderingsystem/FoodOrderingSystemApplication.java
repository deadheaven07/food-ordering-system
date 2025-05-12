package com.foodapp.foodorderingsystem;

import com.foodapp.foodorderingsystem.model.User;
import com.foodapp.foodorderingsystem.model.enums.SelectionStrategyType;
import com.foodapp.foodorderingsystem.service.OrderService;
import com.foodapp.foodorderingsystem.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class FoodOrderingSystemApplication implements CommandLineRunner {

    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== FOOD ORDERING SYSTEM DEMO ===");

        // Onboard restaurants
        restaurantService.onboardRestaurant("R1", 4.5, 5, new HashMap<>(Map.of(
                "Veg Biryani", 100,
                "Chicken Biryani", 150
        )));

        restaurantService.onboardRestaurant("R2", 4.0, 5, new HashMap<>(Map.of(
                "Idli", 10,
                "Dosa", 50,
                "Veg Biryani", 80,
                "Chicken Biryani", 175
        )));

        restaurantService.onboardRestaurant("R3", 4.9, 1, new HashMap<>(Map.of(
                "Idli", 15,
                "Dosa", 30,
                "Gobi Manchurian", 150,
                "Chicken Biryani", 175
        )));
        // Add users with wallet balances
        orderService.getDataStore().users.put("Ashwin", new User("Ashwin", 500));
        orderService.getDataStore().users.put("Harish", new User("Harish", 500));
        orderService.getDataStore().users.put("Shruthi", new User("Shruthi", 500));
        orderService.getDataStore().users.put("Diya", new User("Diya", 100));       // low balance for fail case
        orderService.getDataStore().users.put("SplitUser", new User("SplitUser", 500));

        // Menu updates
        restaurantService.addMenuItems("R1", Map.of("Chicken65", 250));
        restaurantService.updateMenuPrices("R2", Map.of("Chicken Biryani", 150));

        // Place Orders
        System.out.println(orderService.placeOrder("Ashwin", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST));  // R3
        System.out.println(orderService.placeOrder("Harish", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST));  // R2
        System.out.println(orderService.placeOrder("Shruthi", Map.of("Veg Biryani", 3, "Dosa", 1), SelectionStrategyType.HIGHEST_RATING)); // R1

        // Complete an order
        System.out.println(orderService.completeOrder(1)); // R3 free again

        // More orders
        System.out.println(orderService.placeOrder("Harish", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST));  // R3 again
        System.out.println(orderService.placeOrder("Diya", Map.of("Idli", 3, "Paneer Tikka", 1), SelectionStrategyType.LOWEST_COST));  // Cannot assign

        System.out.println("=== DEMO COMPLETE ===");
    }
}
