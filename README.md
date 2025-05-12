# food-ordering-system
Spring Boot based in food ordering system with strategy pattern and order simulation
The system allows restaurants to be onboarded, manage their menus, and process customer orders based on configurable strategies like **Lowest Cost** or **Highest Rating**. All data is maintained in-memory without any external database.

---
## 🚀 Features

- ✅ Onboard new restaurants with rating, menu, and order capacity.
- ✅ Add new items to a restaurant menu.
- ✅ Update prices for existing menu items.
- ✅ Place orders using one of two strategies:
  - `Lowest Cost`
  - `Highest Rating`
- ✅ Prevent overloading restaurants beyond their max active orders.
- ✅ Mark orders as completed to free up capacity.
- ✅ Reject orders that can't be fulfilled by a single restaurant.
- ✅ In-memory wallet balance check (optional enhancement).
- ✅ Support for split orders across multiple restaurants (Strong Hire case).
- ✅ Unit tests using JUnit.

---
## 🛠 Tech Stack

- Java 17
- Spring Boot
- JUnit 5
---
## 📦 Project Structure
src
├── main
│ ├── java
│ │ └── com.foodapp.foodorderingsystem
│ │ ├── controller 
│ │ ├── model # domain classes
│ │ ├── service # business logic
│ │ ├── storage # in-memory datastore
│ │ ├── strategy # strategy pattern implementations
│ │ └── FoodOrderingSystemApplication.java 
├── test
│ └── java
│ └── com.foodapp.foodorderingsystem
│ └── FoodOrderingSystemApplicationTests.java

-----

## ✅ How to Run
./mvnw spring-boot:run
Or
run the main method in FoodOrderingSystemApplication.java.

To execute unit tests:
./mvnw test
run FoodOrderingSystemApplicationTests.java.
