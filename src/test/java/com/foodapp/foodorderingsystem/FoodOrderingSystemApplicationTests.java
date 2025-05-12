package com.foodapp.foodorderingsystem;

import com.foodapp.foodorderingsystem.model.enums.SelectionStrategyType;
import com.foodapp.foodorderingsystem.model.User;
import com.foodapp.foodorderingsystem.service.OrderService;
import com.foodapp.foodorderingsystem.service.RestaurantService;
import com.foodapp.foodorderingsystem.storage.DataStore;
import com.foodapp.foodorderingsystem.strategy.HighestRatingStrategy;
import com.foodapp.foodorderingsystem.strategy.LowestCostStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FoodOrderingSystemApplicationTests  {

    private DataStore dataStore;
    private RestaurantService restaurantService;
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        dataStore = new DataStore();
        restaurantService = new RestaurantService();
        orderService = new OrderService();

        // Inject manually for unit testing
        restaurantService.setDataStore(dataStore);
        orderService.setDataStore(dataStore);
        orderService.setLowestCostStrategy(new LowestCostStrategy());
        orderService.setHighestRatingStrategy(new HighestRatingStrategy());

        // Sample restaurants
        restaurantService.onboardRestaurant("R1", 4.5, 5, new HashMap<>(Map.of(
                "Veg Biryani", 100, "Chicken Biryani", 150
        )));
        restaurantService.onboardRestaurant("R2", 4.0, 5, new HashMap<>(Map.of(
                "Idli", 10, "Dosa", 50, "Veg Biryani", 80, "Chicken Biryani", 175
        )));
        restaurantService.onboardRestaurant("R3", 4.9, 1, new HashMap<>(Map.of(
                "Idli", 15, "Dosa", 30, "Gobi Manchurian", 150, "Chicken Biryani", 175
        )));
        dataStore.users.put("Ashwin", new User("Ashwin", 500));
        dataStore.users.put("Harish", new User("Harish", 500));
        dataStore.users.put("Shruthi", new User("Shruthi", 500));
        dataStore.users.put("Diya", new User("Diya", 100)); // low balance for failure test
        dataStore.users.put("SplitUser", new User("SplitUser", 500));
    }

    @Test
    public void testOrderAssignmentLowestCost() {
        String result = orderService.placeOrder("Ashwin", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST);
        System.out.println(result);
        assertTrue(result.contains("R3"));
    }

    @Test
    public void testSecondOrderGoesToR2() {
        orderService.placeOrder("Ashwin", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST);  // fill R3
        String result = orderService.placeOrder("Harish", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST);
        System.out.println(result);
        assertTrue(result.contains("R2"));
    }

    @Test
    public void testShruthiGetsR1_HighestRating() {
        orderService.placeOrder("Ashwin", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST);  // R3
        orderService.placeOrder("Harish", Map.of("Idli", 3, "Dosa", 1), SelectionStrategyType.LOWEST_COST);  // R2
        String result = orderService.placeOrder("Shruthi", Map.of("Veg Biryani", 3, "Dosa", 1), SelectionStrategyType.HIGHEST_RATING);
        System.out.println(result);
        assertTrue(result.contains("R2"));
    }

    @Test
    public void testInvalidOrderFails() {
        String result = orderService.placeOrder("Diya", Map.of("Idli", 3, "Paneer Tikka", 1), SelectionStrategyType.LOWEST_COST);
        assertTrue(result.contains("Cannot assign"));
    }
    @Test
    public void testSplitOrderAcrossRestaurants() {
        Map<String, Integer> splitItems = Map.of("Idli", 3, "Gobi Manchurian", 1, "Veg Biryani", 2); // not all from one restaurant
        List<String> messages = orderService.placeOrderAcrossRestaurants("SplitUser", splitItems);
        messages.forEach(System.out::println);

        // Optionally assert at least 1 order is placed
        assertTrue(messages.stream().anyMatch(msg -> msg.contains("assigned to")));
    }

}
